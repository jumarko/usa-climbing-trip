(ns usa-google-maps.core
  (:require [reagent.core :as reagent :refer [atom]]
            [usa-google-maps.locations :as l]))


;;; reusable functions for work with google js api
(defn create-lat-lng [location]
  (js/google.maps.LatLng. (:lat location) (:lng location)))

(defn create-marker [map location]
  (js/google.maps.Marker. (clj->js {"position" (create-lat-lng location)
                                    "title" (:name location)
                                    
                                    "map" map})))

(defn create-infowindow [id]
  (js/google.maps.InfoWindow. (clj->js {"content" (str "<div id='info-"id"'></div>")})))

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
  (let [my-map (create-map l/salt-lake-city (reagent/dom-node this))]
    (doseq [location l/locations]
      ;; TODO: add Info Window for each location
      ;; TODO: display custom icons (different for national parks, MUST SEE, etc.)
      (create-marker my-map location))))

(defn map-component []
  (reagent/create-class {:reagent-render map-component-render
                         :component-did-mount map-component-did-mount}))


;;; rendering

(defn mount-root []
  (reagent/render [map-component] (.getElementById js/document "app")))

(defn init! []
  (mount-root))



