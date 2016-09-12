(ns usa-google-maps.locations.weather-data
  (:require [clj-http.client :as http]
            [hickory.core :as h]
            [hickory.select :as hs]
            [net.cgrand.enlive-html :as html]
            ;; TODO: remove database - only for testing
            [usa-google-maps.locations.database :as l]
            ))


(defrecord WeatherData [])


(defn- to-weather-data 
  "Converts to location-weather-data record format.
  Expected input is to be the content of javascript data, that is vector like [['', 107], ['Jan', 171], ..., ['Dec', 107], ['', 171]]
  The redundant records (first one and last one) are removed."
  [weather-data-string]
  (-> weather-data-string
    (clojure.string/replace "'" "\"")
    (read-string)
    (->> 
      (remove #(= "" (nth % 0))))
    ))

(defn get-location-weather
  "Finds location weather data.
  As of now, supports only locations taged as :climbing which should provide the proper value of :uri
  pointing to mountainproject.combat.
  Returns location-weather-data represented as nested map like  ([\"Jan\" 150] [\"Feb\" 250] ...) "
  [location]
  (when (:climbing (:tags location))
    (->> (html/select (html/html-resource (java.net.URL. (:uri location)))
           [:body :script])
      (map :content)
      (map first)
      (filter 
        #(when % (.contains % "drawChartSeason")))
      first
      (re-find #"data.addRows\((.*)\)")
second
to-weather-data)))

(def locations-weather-data (into {} (map (fn [loc] [(:name  loc) (get-location-weather loc)]) l/locations)))

(clojure.pprint/pprint locations-weather-data)

(defn only-last-4-months [locations-weather-data]
(map (fn [[k v]] [k (take-last 4 v)]) locations-weather-data))

(only-last-4-months locations-weather-data)


