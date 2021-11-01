(ns descript-to-video.view.layout
  (:require [hiccup.page :refer [html5 include-css include-js]]))

(defn common [req & body]
  (html5
   [:head
    [:title "descript-to-video-ui"]
    (include-css "/css/normalize.css"
                 "/css/papier.min.css"
                 "/css/style.css")
    (include-js "/js/main.js")]
   [:body
    [:header.top-bar.bg-green.depth-3 "descript-to-video-ui"]
    [:main body]]))