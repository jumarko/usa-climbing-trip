(ns usa-google-maps.middleware
  (:require [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.format :refer [wrap-restful-format]]))

(defn wrap-middleware [handler]
  (-> handler
      wrap-restful-format
      (wrap-defaults site-defaults)
      wrap-exceptions
      wrap-reload))
