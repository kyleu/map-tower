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
map = null

onMapClick = (e) ->
  latlngStr = 'lat: ' + e.latlng.lat.toFixed(4) + '<br/>lng: ' + e.latlng.lng.toFixed(4)
  popup = new L.Popup()
  popup.setLatLng(e.latlng)
  popup.setContent(latlngStr)
  map.openPopup(popup)
  undefined

$ -> 
  center = new L.LatLng(33.7612, -84.3856)

  map = new L.Map('map', { attributionControl: false })
  
  
  styleId = 998
  tileUrl = 'http://{s}.tile.cloudmade.com/0320d0049e1a4242bab7857cec8b343a/' + styleId + '/256/{z}/{x}/{y}.png'
  attrib = 'MapTower!'
  cloudmadeLayer = new L.TileLayer(tileUrl, {
    maxZoom : 18,
    attribution : attrib
  })
  map.setView(center, 18)
  renderMarkers(map.getBounds())
  map.addLayer(cloudmadeLayer)

  loadTestData(center)
  undefined

renderMarker = (p) ->
  marker = new L.Marker(new L.LatLng(p.x, p.y), {icon: if p.tags.size then pointTagsIcon else pointIcon})
  map.addLayer(marker)
  message = "lat: #{p.y}<br/>lng: #{p.x}<br/><br/>\n<strong>Node</strong><br/>\n"
  tagMessages = for k, v of p.tags 
    "#{k}: #{v}"
  message += tagMessages.join("<br/>\n")  
  marker.bindPopup(message)

renderMarkers = (b) ->
  $.getJSON('/sandbox/pathTest', {ul:0, ur:0, br:0, bl:0}, (points) -> $.each(points, (i) -> renderMarker(points[i])))
  undefined

loadTestData = (center) ->
  marker = new L.Marker(new L.LatLng(center.lat - 0.03, center.lng - 0.005), {icon: wayIcon, draggable: true})
  marker.bindPopup("Fuck you, I'm a marker!")
  map.addLayer(marker)

  circleLocation = new L.LatLng(center.lat + 0.02, center.lng + 0.001)
  circleOptions =
    color : 'red',
    fillColor : '#f03',
    fillOpacity : 0.5

  circle = new L.Circle(circleLocation, 500, circleOptions)
  circle.bindPopup("Fuck you, I'm a circle!")
  map.addLayer(circle)

  p1 = new L.LatLng(center.lat - 0.01, center.lng + 0.007)
  p2 = new L.LatLng(p1.lat - 0.006, p1.lng + 0.02)
  p3 = new L.LatLng(p1.lat + 0.001, p1.lng + 0.033)
  polygonPoints = [ p1, p2, p3 ]
  polygon = new L.Polygon(polygonPoints)
  polygon.bindPopup("Fuck you, I'm a polygon!")
  map.addLayer(polygon)

  popup = new L.Popup()
  popup.setLatLng(center)
  popup.setContent("I am a pointless popup.<br/>Because, that's why.")

  map.openPopup(popup)
  
  popup = new L.Popup()

  map.on('click', onMapClick)
  