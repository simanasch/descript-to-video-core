(ns descript-to-video.util.map
  (:require [clojure.walk :as w])
  (:refer  flatland.ordered.map))

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
  [map]
  (w/postwalk #(cond (map? %) (ordered-map %) :else %) map))