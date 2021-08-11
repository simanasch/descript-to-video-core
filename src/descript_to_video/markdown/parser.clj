(ns descript-to-video.markdown.parser
  (:gen-class)
  (:import (com.vladsch.flexmark.util.ast Node)
           (com.vladsch.flexmark.html HtmlRenderer)
           (com.vladsch.flexmark.parser Parser)
           (com.vladsch.flexmark.util.data MutableDataSet)))

(comment
  ;;  https://github.com/vsch/flexmark-java のサンプルから引用
  (def options (new MutableDataSet))
  

  (def parser (. (. Parser builder options) build))


;; HtmlRender
  (def renderer (. (. HtmlRenderer builder options) build))


  (def document (. parser parse "This is *Sparta*"))

  (println (. renderer render document))
  )

