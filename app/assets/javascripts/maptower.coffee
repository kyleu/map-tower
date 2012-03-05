root = this

# Point - x and y are lng and lat doubles.
class Point
  constructor: (@x, @y) ->
  latLng: () -> new L.LatLng(@y, @x)

# Node
class Node
  constructor: (n) ->
    @name = n.name
    @category = n.category
    @subcategory = n.subcategory
    @loc = n.loc
    @note = n.note

  render: (mapView) ->
    if(!@marker)
      @marker = @createMarker()
      mapView.map.addLayer(@marker)
    undefined

  createMarker: () ->
    #ret = new L.Marker(new L.LatLng(@loc.y, @loc.x), {icon: if (tagMessages.length > 0) then pointTagsIcon else pointIcon})
    circleOptions =
      color: 'black',
      fillColor: root.randomColor(),
      fillOpacity: 0.5
    ret = new L.CircleMarker(new L.LatLng(@loc.y, @loc.x), circleOptions)
    message = "lat: #{@loc.y}<br/>lng: #{@loc.x}<br/><br/>\n<strong>#{@name}</strong><br/>\n#{@category}<br/>\n#{@subcategory}<br/><br/>\n"
    message += @note.split(",").join("<br/>\n")
    ret.bindPopup(message)
    ret

# Way
class Way
  constructor: (w) ->
    @name = w.name
    @category = w.category
    @subcategory = w.subcategory
    @note = w.note

    @latlngs = new Array
    for i of w.points
      @latlngs.push(new L.LatLng(w.points[i].y, w.points[i].x))

  render: (mapView) ->
    if(!@way)
      @way = @createWay()
      mapView.map.addLayer(@way)
    undefined

  createWay: () ->
    ret =  new L.Polyline(@latlngs, {color: root.randomColor()})
    message = "Way (#{@latlngs.length} points)<br/><br/>\n<strong>#{@name}</strong><br/>\n#{@category}<br/>\n#{@subcategory}<br/><br/>\n"
    message += @note.split(",").join("<br/>\n")
    ret.bindPopup(message)
    ret

# Handles server communication, game state, input, animation, other shizzle. Should break this class up soon.
class MapTower
  constructor: (@gameType) ->
    @gameType.initialCenter = new Point(@gameType.initialCenter.x, @gameType.initialCenter.y)

  nodeCache: {}
  wayCache: {}
  mapView: null

  clear: () ->
    # clear, remove nodes/ways, reset game

  initView: (divId) ->
    @mapView = new MapView(divId, @gameType.initialCenter, @gameType.initialZoom)
    @update(@mapView.map.getBounds())
    @mapView.addTileLayer()

    @infoPanel = new InfoPanel(@)
    @actionsPanel = new ActionsPanel(@)
    @debugPanel = new DebugPanel(@)
    @optionsPanel = new OptionsPanel(@)

  networkCallback: (rsp) =>
    @addNode node for node in rsp.nodes
    @addWay way for way in rsp.ways

  update: (b) =>
    ul = b.getNorthWest()
    br = b.getSouthEast()
    params = {"min.x": ul.lng, "min.y": br.lat, "max.x": br.lng, "max.y": ul.lat}

    $.get('/game/' + @gameType.code + '/data', params, @networkCallback, "json")
    undefined

  addNode: (obj) =>
    if @nodeCache[obj.osmId]
      console.warn("Encountered cached node on update: ", obj)
    else
      node = new Node(obj)
      @nodeCache[node.osmId] = node
      node.render(@mapView)
    undefined

  addWay: (obj) =>
    if @wayCache[obj.osmId]
      console.warn("Encountered cached way on update: ", obj)
    else
      way = new Way(obj)
      @wayCache[way.osmId] = way
      way.render(@mapView)
    undefined

# Contains all Leaflet interactions, caches map data
class MapView
  constructor: (id, center, zoom) -> 
    @map = new L.Map('map', { attributionControl: false, zoomControl: false })
    @map.setView(center.latLng(), zoom)
    @map.on('click', @onMapClick)
    @map.on('zoomend', @onMapZoom)

  zoomIn: () => @map.zoomIn()
  zoomOut: () => @map.zoomOut()

  addTileLayer: () =>
    # tileUrl = 'http://{s}.tile.cloudmade.com/0320d0049e1a4242bab7857cec8b343a/998/256/{z}/{x}/{y}.png'
    tileUrl = '/tiles/{z}/{x}/{y}'
    tileLayer = new L.TileLayer(tileUrl, {
      maxZoom : 18,
    })
    @map.addLayer(tileLayer)

  onMapClick: (e) =>
    latlngStr = 'lat: ' + e.latlng.lat.toFixed(4) + '<br/>lng: ' + e.latlng.lng.toFixed(4)
    popup = new L.Popup()
    popup.setLatLng(e.latlng)
    popup.setContent(latlngStr)
    @map.openPopup(popup)
    undefined

  onMapZoom: (e) => 
    # MapTower.update(@map.getBounds()) 

class CustomIcon extends L.Icon
  constructor: (@iconUrl) ->
  shadowUrl: '/assets/images/map/shadow.png'
  iconSize: new L.Point(38, 95)
  shadowSize: new L.Point(68, 95)
  iconAnchor: new L.Point(22, 94)
  popupAnchor: new L.Point(-3, -76)

pointIcon = new CustomIcon '/assets/images/map/point.png'
pointTagsIcon = new CustomIcon '/assets/images/map/point-tags.png'
wayIcon = new CustomIcon '/assets/images/map/way.png'

# Game panels
class Panel
  constructor: (@id) -> 
    @content = $("#" + @id + "-panel")

    callback = (e) =>
      msg = e.srcElement.value
      console.log("Reacting to #{msg} from #{@id}:", e)
      @react msg
    @content.find("button").click(callback)
    @content.find("input").click(callback)

  react: (msg) =>
    console.log "Unhandled message #{msg} from #{@id}."

class InfoPanel extends Panel
  constructor: () -> 
    super "info"

class ActionsPanel extends Panel
  constructor: (@mt) -> 
    super "actions"

  react: (msg) =>
    if(msg == "zoom-in")
      @mt.mapView.zoomIn()
    else if(msg == "zoom-out")
      @mt.mapView.zoomOut()
    else
      super.react(msg)

class DebugPanel extends Panel
  constructor: () -> 
    super "debug"

class OptionsPanel extends Panel
  constructor: () -> 
    super "options"

$ -> 
  mapTower = new MapTower(root.gameType)
  mapTower.initView("map")
  undefined
