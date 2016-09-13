(ns usa-google-maps.locations
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :as a :refer [<! >!]]))

(defn fetch-locations []
  (go (let [response (<!  (http/get "/locations"
                            {:accept "application/edn"}))]
        (:body response))))

