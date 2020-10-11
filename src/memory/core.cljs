(ns ^:figwheel-hooks memory.core
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]))

(defn get-app-element []
  (gdom/getElement "app"))

(defn card [content]
  [:div.tile.is-child.box
   [:p content]])

(defn card-row [row]
  (into [:div.tile.is-parent.is-vertical] (map card row)))

(defn board [items]
  (let [partitions (partition 4 items)
        rows (map card-row partitions)]
    (into [:div.tile.is-ancestor.has-text-centered
           rows])))

(defn memory []
  [:div
   [:h1.title "Memory game"]
   (board (map str (range 1 17)))])

(defn mount [el]
  (rdom/render [memory] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

(mount-app-element)

(defn ^:after-load on-reload []
  (mount-app-element))
