(ns usa-google-maps.prod
  (:require [usa-google-maps.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
