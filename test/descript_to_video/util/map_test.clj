(ns descript-to-video.util.map-test
  (:require [clojure.test :refer :all]
            [descript-to-video.util.map :refer :all]
            [flatland.ordered.map :refer :all]))

(deftest deep-merge-with-test
  (testing "deep-merge-withのテスト"
    (is 
     (=
      {:a {:b {:z 3, :c 3, :d {:z 9, :x 1, :y 2}}, :e 103}, :f 4}
      (deep-merge-with + {:a {:b {:c 1 :d {:x 1 :y 2}} :e 3} :f 4}
                       {:a {:b {:c 2 :d {:z 9} :z 3} :e 100}}))))
  (testing "map->ordered-mapのテスト"
    (is
     (=
      ordered-map
      (map->ordered-map {})))))

(comment
  ;; このnamespace内のテストを実行する
  (run-tests)
  )