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

mapView = null

testData = (center, mapView) ->
  marker = new L.Marker(new L.LatLng(center.lat - 0.03, center.lng - 0.005), {icon: pointIcon, draggable: true})
  marker.bindPopup("Fuck you, I'm a marker!")
  mapView.map.addLayer(marker)

  circleLocation = new L.LatLng(center.lat + 0.02, center.lng + 0.001)
  circleOptions =
    color : 'red',
    fillColor : '#f03',
    fillOpacity : 0.5

  circle = new L.Circle(circleLocation, 500, circleOptions)
  circle.bindPopup("Fuck you, I'm a circle!")
  mapView.map.addLayer(circle)

  p1 = new L.LatLng(center.lat - 0.01, center.lng + 0.007)
  p2 = new L.LatLng(p1.lat - 0.006, p1.lng + 0.02)
  p3 = new L.LatLng(p1.lat + 0.001, p1.lng + 0.033)
  polygonPoints = [ p1, p2, p3 ]
  polygon = new L.Polygon(polygonPoints)
  polygon.bindPopup("Fuck you, I'm a polygon!")
  mapView.map.addLayer(polygon)

  popup = new L.Popup()
  popup.setLatLng(center)
  popup.setContent("I am a pointless popup.<br/>Because, that's why.")

  mapView.map.openPopup(popup)

# Handles server communication
MapNetwork =
  nodeCache: {}
  wayCache: {}

  update: (b) =>
    ul = b.getNorthWest()
    br = b.getSouthEast()
    params = {"ul.x": ul.lng, "ul.y": ul.lat, "br.x": br.lng, "br.y": br.lat}
    callback = (rsp) -> 
      $.each(rsp.nodes, (i) ->
        node = rsp.nodes[i]
        if MapNetwork.nodeCache[node.osmId]
          console.log("Cached node on update: ", node)
          return
        MapNetwork.nodeCache[node.osmId] = node
        mapView.renderNode(node))
      $.each(rsp.ways, (i) -> 
        mapView.renderWay(rsp.ways[i]))
    $.get('/sandbox/pathTest', params, callback, "json")
    undefined

# Contains all Leaflet interactions, caches map data
class MapView
  constructor: (id, center, zoom) -> 
    @map = new L.Map('map', { attributionControl: false })
    @map.setView(center, 17)
    @map.on('click', @onMapClick)
    @map.on('zoomend', @onMapZoom)

  addTileLayer: () =>
    
    tileUrl = 'http://{s}.tile.cloudmade.com/0320d0049e1a4242bab7857cec8b343a/998/256/{z}/{x}/{y}.png'
    # tileUrl = '/tiles/{z}/{x}/{y}'
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
    # MapNetwork.update(@map.getBounds())

  renderNode: (p) =>
    tagMessages = for k, v of p.tags 
      "#{k}: #{v}"
    if (tagMessages.length > 0)
      marker = new L.Marker(new L.LatLng(p.loc.y, p.loc.x), {icon: if p.tags.size then pointTagsIcon else pointIcon})
      @map.addLayer(marker)
      message = "lat: #{p.loc.y}<br/>lng: #{p.loc.x}<br/><br/>\n<strong>Node</strong><br/>\n"
      message += tagMessages.join("<br/>\n")  
      marker.bindPopup(message)
    undefined

  renderWay: (w) =>
    tagMessages = for k, v of w.tags 
      "#{k}: #{v}"

    if (tagMessages.length > 0)
      latlngs = new Array
      for k, v of w.nodeIds 
        node = MapNetwork.nodeCache[v]
        if node 
          latlngs.push(new L.LatLng(node.loc.y, node.loc.x))

      way =  new L.Polyline(latlngs, {color: 'red'});
      @map.addLayer(way)
      message = "<trong>Way</strong><br/>\n"
      message += tagMessages.join("<br/>\n")  
      way.bindPopup(message)
    undefined

$ -> 
  center = new L.LatLng(33.7612, -84.3856)

  mapView = new MapView("map", center, 17)
  MapNetwork.update(mapView.map.getBounds())
  testData(center, mapView)
  mapView.addTileLayer()
  undefined
