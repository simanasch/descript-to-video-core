(ns descript-to-video.util.map
  (:require [clojure.walk :as w]
            [flatland.ordered.map :refer [ordered-map]]))

(defn deep-merge-with
  "contribにあったものの移植、ネストしたmapに対して再帰的にmerge-withをコールする"
  [f & maps]
  (apply
   (fn m [& maps]
     (if (every? map? maps)
       (apply merge-with m maps)
       (apply f maps)))
   maps))

(defn map->ordered-map
  "ネストしたmapに対し、再帰的にmapからordered-mapに詰め替えする"
  [map]
  (w/postwalk #(cond (map? %) (ordered-map %) :else %) map))