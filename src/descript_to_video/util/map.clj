(ns descript-to-video.util.map)

(defn deep-merge-with
  "contribにあったものの移植、ネストしたmapに対して再帰的にmerge-withをコールする"
  [f & maps]
  (apply
   (fn m [& maps]
     (if (every? map? maps)
       (apply merge-with m maps)
       (apply f maps)))
   maps))