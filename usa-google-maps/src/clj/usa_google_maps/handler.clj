(ns usa-google-maps.handler
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [usa-google-maps.middleware :refer [wrap-middleware]]
            [config.core :refer [env]]
            [usa-google-maps.locations.database :as ld]))

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


(defn locations-page []
  {:status 200
   :body ld/locations})

(defroutes routes
  (GET "/" [] (loading-page))
  (GET "/about" [] (about-page))
  (GET "/locations" [] (locations-page))

  (resources "/")
  (not-found "Not Found"))

(def app (wrap-middleware #'routes))
