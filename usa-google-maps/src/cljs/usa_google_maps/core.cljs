(ns usa-google-maps.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as r]
            [usa-google-maps.locations :as l]
            [cljs.core.async :as a :refer [<!]]
            [clojure.string :as str]))

;;; locations data
(defonce app-db (r/atom {}))


;;; date formatting functions
(defn- format-date [date-str]
  (->  (apply str (take 6 date-str))
    (str/replace "-"  ".")))

(defn- arrival-date [location]
  (-> location
    :details
    :arrival
    format-date))

(defn- departure-date [location]
  (-> location
    :details
    :departure
    format-date))


;;; reusable functions for work with google js api
(defn create-lat-lng [location]
  (js/google.maps.LatLng. (:lat location) (:lng location)))

(def marker-icons
  {:climbing "images/climbing_marker_32x32.png" ;; check https://thenounproject.com/term/rock-climbing/529/
   :national-park "images/national_park_marker_32x32.png" ;; check https://thenounproject.com/search/?q=national+park&i=158731
   :state-park "images/state_park_marker_32x32.png"
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
        (:state-park tags) (assoc default-marker-options "icon" (:state-park marker-icons))
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
                                            "<p>"
                                            "<a target='_blank' href='"
                                            (:uri location) "'>" (:name location) "</a></br>"
                                            "Arrival: " (arrival-date location)
                                            "; Departure: " (departure-date location)
                                            "</p>"
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


(defn render-road-trip [road-trip map-component]
  (let [locations-as-lat-lng-pairs (map (fn [loc] {"lat" (:lat loc) "lng" (:lng loc)})
                                     road-trip)
        polyline-opts (clj->js {"path" locations-as-lat-lng-pairs
                                "geodesic" true
                                "strokeColor" "RED"
                                "strokeOpacity" 1.0
                                "strokeWeight" 2})
        polyline (js/google.maps.Polyline. polyline-opts)]
    (.setMap polyline map-component)))


;;; map component
(defn map-component-render []
  [:div {:style {:position "relative"
                 :width "1280px"
                 :height "800px"
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

marker-icons
(defn map-legend-component []
  [:div#map-legend
   [:table
    [:tbody
     (for [[tag, icon-img] marker-icons]
       ^{:key tag} [:tr [:td [:img {:src icon-img}]] [:td tag]])]]
   [:p "Click icons to see more details"]])

(defn show-route-component []
  (let [road-trip (:road-trip @app-db)]
    [:div#show-route
     [:p [:i "Start: "] [:b (arrival-date (first road-trip)) " " (:name (first road-trip))]]
     [:p [:i  "Waypoints: "] [:br] (clojure.string/join " -> "
                                     (map  #(str (arrival-date %) " " (:name %)) (subvec  road-trip 1 (dec (count road-trip)))))]
     [:p [:i "End: "] [:b (arrival-date (last road-trip)) " " (:name (last road-trip))]]]
    ))

(defn total-distance-component [total-distance]
  [:p "Total distance (in meters): " total-distance])

(defn main-component []
  [:div
   [show-route-component]
   ;; we are not able to calculate total distance now - we just plot simple polylines
   ;; among the waypoints
   #_[:div [total-distance-component (:road-trip-distance @app-db)]]
   [:div#map [map-component]]
   [map-legend-component]])

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

