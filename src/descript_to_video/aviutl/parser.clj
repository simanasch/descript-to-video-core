(ns descript-to-video.aviutl.parser
  (:require [instaparse.core :as insta]))

(def object-parser
  (insta/parser
   "S = exedit object+ 
      exedit = #'\\[exedit\\]\r\n' property+
      property = name <'='> value  <CRLF>
      name = #'[^\\[=]+'
      value = #'.+'
      <CRLF> = #'\r\n' 
      object = <'['> layer <']'> <CRLF> property+ effect* object | Epsilon
      effect = <'['> layer <'.'> order <']'> CRLF property+
      layer = #'\\d+'
      order = #'\\d+'
      "))

(comment
  (def raw-project-file (slurp "E:/Documents/descript-to-video/output/template.exo" :encoding "shift-jis"))
  (def objects (clojure.string/split raw-project-file #"\]\r\n"))
  (nth objects 3)

  (def raw-object (slurp "./resources/alias/default.exo" :encoding "shift-jis"))
  (def parsed-object (object-parser raw-object))
  (clojure.pprint/pprint parsed-object)
  parsed-object
  )