(ns descript-to-video.aviutl.parser
  (:require [instaparse.core :as insta]))

(def aviutl-exobject-format
  (insta/parser 
   "exo=project layer+
    project = \"\[exedit\]\"CRLF
    CRLF=\r\n"))

(comment
  
  (def raw-project-file (slurp "E:/Documents/descript-to-video/output/template.exo" :encoding "shift-jis"))
  (def objects (clojure.string/split raw-project-file #"\]\r\n"))
  (nth objects 3)
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
  (def raw-object (slurp "./resources/alias/default.exo" :encoding "shift-jis"))
  (def parsed-object (object-parser raw-object))
  (clojure.pprint/pprint parsed-object)
  parsed-object
  )