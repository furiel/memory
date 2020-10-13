(ns ^:figwheel-hooks memory.core
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [clojure.set :as set]
   [re-frisk.core :as re-frisk]
   [re-frame.core :as rf]
   [re-frame.db :as db]))

(def CARDS
  [[:i.fas.fa-3x.fa-cat]
   [:i.fas.fa-3x.fa-dog]
   [:i.fas.fa-3x.fa-dove]
   [:i.fas.fa-3x.fa-feather]
   [:i.fas.fa-3x.fa-fish]
   [:i.fas.fa-3x.fa-paw]
   [:i.fas.fa-3x.fa-spider]
   [:i.fas.fa-3x.fa-dragon]])

(defn get-app-element []
  (gdom/getElement "app"))

(defn update-locked [db]
  (if (= (-> db :cards :selected count) 2)
    (let [selected (-> db :cards :selected)]
      (if (apply = (vals selected))
        (update-in db [:cards :locked] set/union (set (keys selected)))
        db))
    db))

(set/union nil (set (keys {:a :b})))

(defn update-selected [db id card-id]
  (let [selected-count (-> db :cards :selected count)]
    (if (< selected-count 2)
      (update-in db [:cards :selected] merge {id card-id})
      (assoc-in db [:cards :selected] {id card-id}))))

(defn victory? [db]
  (when (= (* 2 (count CARDS))
           (count (-> db :cards :locked)))
    (rf/dispatch [:victory]))
  db)

(rf/reg-event-db
 :victory
 (fn [db _]
   (js/alert "Victory")
   db))

(defn update-db [db [:selected id card-id]]
  (-> db
      (update-selected id card-id)
      (update-locked)
      victory?))

(rf/reg-event-db
 :selected
 update-db)

(rf/reg-event-db
 :reset-db
 (constantly {}))

(rf/reg-sub
 :cards
 (fn [db _]
   (:cards db)))

(defn is-invisible [cards id]
  (or (contains? (get cards :selected) id)
      (contains? (get cards :locked) id)))

(defn card-item [_]
  (let [cards (rf/subscribe [:cards])]
    (fn [[id {content :content card-id :card-id}]]
      [:div.tile.is-child.box.card-item
       {:on-click (fn [e]
                    (.preventDefault e)
                    (rf/dispatch [:selected id card-id]))}
       [:p {:class (when-not (is-invisible @cards id) "is-invisible")} content]])))

(defn board [items]
  (let [partitions (partition 4 (map-indexed vector items))]
    (into
     [:div.tile.is-ancestor.has-text-centered
      (for [[index column] (map-indexed vector partitions)]
        (into
         [:div.tile.is-parent.is-vertical {:key index}
          (for [[id card] column]
            ^{:key id} [card-item [id card]])]))])))

(defn generate-cards [contents]
  (letfn [(twice [coll] (concat coll coll))]
    (let [cards (into []
                      (map-indexed
                       (fn [card-id content]
                         {:card-id card-id :content content}))
                      contents)]
      (twice cards))))

(defn memory []
;  (re-frisk/enable)
  [:div.mx-4
   [:h1.title.mx-4 "Memory game"]
   [board (generate-cards CARDS)]
   [:button.mx-4 {:class "button" :style {:background-color "silver"}
                  :on-click (fn [e]
                              (.preventDefault e)
                              (rf/dispatch [:reset-db])
                              )} "Restart"]
])

(defn mount [el]
  (rdom/render [memory] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

(mount-app-element)

(defn ^:after-load on-reload []
  (mount-app-element))
