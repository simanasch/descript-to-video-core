(ns descript-to-video.aviutl.parser)

(comment
  
  (def raw-project-file (slurp "E:/Documents/descript-to-video/output/template.exo" :encoding "shift-jis"))
  (def objects (clojure.string/split raw-project-file #"\]\r\n"))
  (nth objects 3)
  )