class Point
  constructor: (@x, @y) ->

class Node
  constructor: (n) ->
    console.log(n)
    @name = n.name
    @category = n.category
    @loc = n.loc
    @tags = n.tags

  render: (mapView) ->
    tagMessages = for k, v of @tags 
      "#{k}: #{v}"
    @marker = new L.Marker(new L.LatLng(@loc.y, @loc.x), {icon: if (tagMessages.length > 0) then pointTagsIcon else pointIcon})
    @message = "lat: #{@loc.y}<br/>lng: #{@loc.x}<br/><br/>\n<strong>#{@name}</strong><br/>\n#{@category}<br/><br/>\n"
    @message += tagMessages.join("<br/>\n")
    @marker.bindPopup(@message)
    mapView.map.addLayer(@marker)
    undefined

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

# Handles server communication
class MapTower
  constructor: (@gameId) ->
  
  nodeCache: {}
  wayCache: {}
  mapView: null

  initView: (id, center, zoom) ->
    @mapView = new MapView(id, center, zoom)
    @update(@mapView.map.getBounds())
    @mapView.addTileLayer()

  update: (b) =>
    ul = b.getNorthWest()
    br = b.getSouthEast()
    params = {"min.x": ul.lng, "min.y": br.lat, "max.x": br.lng, "max.y": ul.lat}

    $.get('/game/' + @gameId + '/data', params, @networkCallback, "json")
    undefined

  networkCallback: (rsp) =>
    @addNode node for node in rsp.nodes
    @mapView.renderWay way for way in rsp.ways

  addNode: (obj) =>
    if @nodeCache[obj.osmId]
      console.warn("Encountered cached node on update: ", obj)
    else
      node = new Node(obj)
      @nodeCache[node.osmId] = node
      node.render(@mapView)
    undefined


# Contains all Leaflet interactions, caches map data
class MapView
  constructor: (id, center, zoom) -> 
    @map = new L.Map('map', { attributionControl: false })
    @map.setView(center, zoom)
    @map.on('click', @onMapClick)
    @map.on('zoomend', @onMapZoom)

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

  renderWay: (w) =>
    tagMessages = for k, v of w.tags 
      "#{k}: #{v}"

    latlngs = new Array
    for i of w.points
      latlngs.push(new L.LatLng(w.points[i].y, w.points[i].x))

    way =  new L.Polyline(latlngs, {color: 'red'})
    @map.addLayer(way)
    message = "Way (#{w.points.length} points)<br/><br/>\n<strong>#{w.name}</strong><br/>\n#{w.category}<br/><br/>\n"
    message += tagMessages.join("<br/>\n")
    way.bindPopup(message)
    undefined

$ -> 
  center = new L.LatLng(33.7612, -84.3856)
  mapTower = new MapTower("atlanta")
  mapTower.initView("map", center, 16)
  undefined
