(ns memory.core-test
    (:require
     [cljs.test :refer-macros [deftest is testing run-tests run-tests]]
     [memory.core :refer [update-selected update-locked]]))

(deftest test-update-selected
  (is (= (update-selected {} 1 "1")
         {:cards {:selected {1 "1"}}}))
  (is (= (update-selected {:cards {:selected {1 "1"}}} 2 "2")
         {:cards {:selected {1 "1", 2 "2"}}}))
  (is (= (update-selected {:cards {:selected {1 "1" 2 "2"}}} 3 "3")
         {:cards {:selected {3 "3"}}})))

(deftest test-update-locked
  (is (= (update-locked {:cards {:selected {1 "1"}}})
         {:cards {:selected {1 "1"}}}))
  (is (= (->
          (update-locked {:cards {:selected {1 :same 2 :same}}})
          :cards
          :locked)
         #{1 2}))
  (is (= (->
          (update-locked {:cards {:locked #{3 4} :selected {1 :same 2 :same}}})
          :cards
          :locked)
         #{1 2 3 4})))

(run-tests)
