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
mapNetwork = null

# Handles server communication
class MapNetwork
  constructor: () ->
    
  updateNodes: (b) =>
    ul = b.getNorthWest()
    br = b.getSouthEast()
    params = {"ul.x": ul.lng, "ul.y": ul.lat, "br.x": br.lng, "br.y": br.lat}
    callback = (nodes) -> 
      $.each(nodes, (i) -> mapView.renderNode(nodes[i]))
    $.post('/sandbox/pathTest', params, callback, "json")
    undefined

  loadTestData: (center) =>
    marker = new L.Marker(new L.LatLng(center.lat - 0.03, center.lng - 0.005), {icon: mapView.wayIcon, draggable: true})
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

# Contains all Leaflet interactions, caches map data
class MapView
  constructor: (id, center, zoom) -> 
    @map = new L.Map('map', { attributionControl: false })
    @map.setView(center, 17)
    @map.on('click', @onMapClick)
    @map.on('zoomend', @onMapZoom)

  addTileLayer: () =>
    styleId = 998
    tileUrl = 'http://{s}.tile.cloudmade.com/0320d0049e1a4242bab7857cec8b343a/' + styleId + '/256/{z}/{x}/{y}.png'
    tileLayer = new L.TileLayer(tileUrl, {
      maxZoom : 18,
      attribution : ""
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
    renderNodes(@map.getBounds())

  renderNode: (p) =>
    tagMessages = for k, v of p.tags 
      "#{k}: #{v}"
    if (tagMessages.length > 0)
      marker = new L.Marker(new L.LatLng(p.loc.x, p.loc.y), {icon: if p.tags.size then pointTagsIcon else pointIcon})
      @map.addLayer(marker)
      message = "lat: #{p.loc.y}<br/>lng: #{p.loc.x}<br/><br/>\n<strong>Node</strong><br/>\n"
      message += tagMessages.join("<br/>\n")  
      marker.bindPopup(message)
    undefined

$ -> 
  center = new L.LatLng(33.7612, -84.3856)

  mapView = new MapView("map", center, 17)
  mapNetwork = new MapNetwork()
  mapNetwork.updateNodes(mapView.map.getBounds())

  mapView.addTileLayer()

  #loadTestData(center)
  undefined
