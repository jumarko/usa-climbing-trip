(ns usa-google-maps.handler
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [usa-google-maps.middleware :refer [wrap-middleware]]
            [config.core :refer [env]]
            [usa-google-maps.locations.database :as ld]
            [ring.middleware.format :refer [wrap-restful-format]]))

(def mount-target
  [:div#app
      [:h3 "ClojureScript has not been compiled!"]
      [:p "please run "
       [:b "lein figwheel"]
       " in order to start the compiler"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
    (head)
    [:body {:class "body-container"}
     mount-target
     (include-js "/js/app.js")]))

(defn about-page []
  (html5
    [:div
     [:p [:h1 "ABOUT"]]]))


(defn locations-resource []
  {:status 200
   ;; need to convert body to map because client side doesn't have access to defrecord Location definition
   :body (map #(into {} %) ld/locations)})

(defn road-trip-resource []
  {:status 200
   ;; need to convert body to map because client side doesn't have access to defrecord Location definition
   :body (map #(into {} %) ld/road-trip)})


(defroutes routes
  (GET "/" [] (loading-page))
  (GET "/about" [] (about-page))
  (GET "/locations" [] (locations-resource))
  (GET "/road-trip" [] (road-trip-resource))

  (resources "/")
  (not-found "Not Found"))

(def app (-> (wrap-restful-format #'routes)
           (wrap-middleware)))
