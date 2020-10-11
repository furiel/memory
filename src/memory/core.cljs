(ns ^:figwheel-hooks memory.core
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]))

(defn get-app-element []
  (gdom/getElement "app"))

(defn memory []
  [:div
   [:h1 "Memory game"]])

(defn mount [el]
  (rdom/render [memory] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

(mount-app-element)

(defn ^:after-load on-reload []
  (mount-app-element))
