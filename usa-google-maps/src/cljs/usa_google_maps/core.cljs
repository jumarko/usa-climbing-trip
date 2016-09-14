(ns usa-google-maps.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as r]
            [usa-google-maps.locations :as l]
            [cljs.core.async :as a :refer [<!]]))
;;; locations data
(defonce locations (r/atom []))

;;; reusable functions for work with google js api
(defn create-lat-lng [location]
  (js/google.maps.LatLng. (:lat location) (:lng location)))

(def marker-icons
  {:climbing "images/climbing_marker_32x32.png" ;; check https://thenounproject.com/term/rock-climbing/529/
   :national-park "images/national_park_marker_32x32.png" ;; check https://thenounproject.com/search/?q=national+park&i=158731
   :city "images/city_marker_32x32.png" ;; check http://megaicons.net/static/img/icons_title/22/119/title/city-building-icon.png
   })

(defn marker-options [map location]
  (let [default-marker-options {"position" (create-lat-lng location)
                                "title" (:name location)
                                "map" map}]
    (if-let [tags (:tags location)]
      (cond
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
                           "mapTypeId" "roadmap"})]
    (js/google.maps.Map. dom-element map-opts)))


;;; map component
(defn map-component-render []
  [:div {:style {:width "1280px"
                 :height "1080px"
                 :align "middle"}}])

(defn map-component-did-mount [this]
  (let [locs @locations
        salt-lake-city (some #(when (= (:name %) "Salt Lake City") %) locs)]
    (if salt-lake-city
      (let [my-map (create-map salt-lake-city (r/dom-node this))]
        (doseq [loc locs]
          (->> (create-marker my-map loc)
            (create-info-window my-map loc)))))))

(defn map-component []
  (r/create-class {:reagent-render map-component-render
                   :component-did-mount map-component-did-mount
                   :display-name "map component"}))


;;; rendering
(defn mount-root []
  (r/render [map-component] (.getElementById js/document "app")))

;; fetch locations, then render map component
(defn fetch-data-and-render []
  (go (let [locs (<! (l/fetch-locations))]
        (reset! locations locs)
        (mount-root))))

(defn init! []
  ;; do nothing - the rendering is postponed until locations are fetched
  (fetch-data-and-render))

