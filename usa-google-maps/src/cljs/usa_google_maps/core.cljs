(ns usa-google-maps.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as r]
            [usa-google-maps.locations :as l]
            [cljs.core.async :as a :refer [<!]]))

;;; locations data
(defonce app-db (r/atom {}))

;;; reusable functions for work with google js api
(defn create-lat-lng [location]
  (js/google.maps.LatLng. (:lat location) (:lng location)))

(def marker-icons
  {:climbing "images/climbing_marker_32x32.png" ;; check https://thenounproject.com/term/rock-climbing/529/
   :national-park "images/national_park_marker_32x32.png" ;; check https://thenounproject.com/search/?q=national+park&i=158731
   :city "images/city_marker_32x32.png" ;; check http://megaicons.net/static/img/icons_title/22/119/title/city-building-icon.png
   :climbing-national-park "images/climbing_and_national_park_marker_32x32.png" ;; custom icon
   })

(defn marker-options [map location]
  (let [default-marker-options {"position" (create-lat-lng location)
                                "title" (:name location)
                                "map" map}]
    (if-let [tags (:tags location)]
      (cond
        (and (:climbing tags) (:national-park tags)) (assoc default-marker-options "icon" (:climbing-national-park marker-icons))
        (:climbing tags) (assoc default-marker-options "icon" (:climbing marker-icons))
        (:national-park tags) (assoc default-marker-options "icon" (:national-park marker-icons))
        (:city tags)(assoc default-marker-options "icon" (:city marker-icons))
        :else default-marker-options)
      default-marker-options)))

(defn create-marker
  "Creates new marker attached to the given map at given location.
  Check marker documentation: 
    main: https://developers.google.com/maps/documentation/javascript/markers
    custom markers: https://developers.google.com/maps/documentation/javascript/tutorials/custom-markers"
  [map location]
  (js/google.maps.Marker. (clj->js (marker-options map location))))


(defn create-info-window
  "Creates new info window for given location attached to given marker."
  [map location marker]
  (let [info-window (js/google.maps.InfoWindow.
                      (clj->js {"content" (str "<div>"
                                            "<p><a target='_blank' href='"
                                            (:uri location) "'>" (:name location) "</a></p>"
                                            "</div>")}))]
    (js/google.maps.event.addListener
      marker
      "click"
      #(.open info-window map marker))))

(defn create-map [location dom-element]
  (let [map-opts (clj->js {"center" (create-lat-lng location)
                           "zoom" 6
                           "mapTypeId" "roadmap"
                           "scaleControl" true})]
    (js/google.maps.Map. dom-element map-opts)))

(defn to-location-string [location]
  (str (:lat location) " " (:lng location)))

(defn render-road-trip [road-trip map-component]
  (let [directions-service  (js/google.maps.DirectionsService.)
        directions-display (doto  (js/google.maps.DirectionsRenderer.)
                             (.setMap map-component))
        origin (first road-trip)
        destination (last road-trip)
        ;; at most 8 waypoints are allowed -> check https://developers.google.com/maps/documentation/javascript/directions#waypoint-limits
        waypoints (subvec road-trip 1 (min 9 (dec (count road-trip))))
        direction-request (clj->js {:origin (to-location-string origin)
                                    :destination (to-location-string destination)
                                    :waypoints (map (fn [location] {:location (to-location-string location)})
                                                 waypoints)
                                    :travelMode "DRIVING"})]
    (.route directions-service
      direction-request
      (fn [result status]
        (if (= "OK" status)
          (do 
            (.setDirections directions-display result)
            (let [legs (-> result
                         (.-routes)
                         (aget 0)
                         (.-legs))
                  legs-distances (map #(.-value (.-distance %)) legs)
                  total-distance-in-meters (apply + legs-distances)]
              (swap! app-db assoc :road-trip-distance total-distance-in-meters)))
          (js/alert (str  "Route planning error! status=" status)))
        ))))


;;; map component
(defn map-component-render []
  [:div {:style {:position "relative"
                 :width "1920px"
                 :height "1080px"
                 :align "center"}}])

(defn map-component-did-mount [this]
  (let [locs (:locations  @app-db)
        salt-lake-city (some #(when (= (:name %) "Salt Lake City") %) locs)]
    (if salt-lake-city
      (let [my-map (create-map salt-lake-city (r/dom-node this))]
        (doseq [loc locs]
          (->> (create-marker my-map loc)
            (create-info-window my-map loc)))
        (render-road-trip (:road-trip @app-db) my-map)))))

(defn map-component []
  (r/create-class {:reagent-render map-component-render
                   :component-did-mount map-component-did-mount
                   :display-name "map component"}))

(defn show-route-component []
  (let [road-trip (:road-trip @app-db)]
    [:div 
     [:p [:i "Start: "] [:b  (:name (first road-trip))]]
     [:p [:i  "Displayed waypoints: "] [:b  (clojure.string/join " -> " (map  #(:name %) (subvec road-trip 1 9)))]]
     [:p [:i "Waypoints not shown: "] (clojure.string/join " -> " (map  #(:name %) (subvec road-trip 9 (dec (count road-trip)))))]
     [:p [:i "End: "] [:b  (:name (last road-trip))]]]
    ))

(defn total-distance-component [total-distance]
  [:p "Total distance (in meters): " total-distance])
(defn main-component []
  [:div 
   [:div#show-route [show-route-component]]
   [:div [total-distance-component (:road-trip-distance @app-db)]]
   [:div#map [map-component]]])

;;; rendering
(defn mount-root []
  (r/render [main-component] (.getElementById js/document "app")))

;; fetch data, then render map component
(defn fetch-data-and-render []
  (go (let [locs (vec (<! (l/fetch-locations)))
            road-trip (vec (<! (l/fetch-road-trip)))]
        (reset! app-db {:locations locs
                        :road-trip road-trip})
        (mount-root))))

(defn init! []
  ;; do nothing - the rendering is postponed until locations are fetched
  (fetch-data-and-render))

