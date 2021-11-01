(ns descript-to-video.util.response
  (:require [ring.util.response :as res]))

(def response #'res/response)
(alter-meta! #'response #(merge % (meta #'res/response)))

(defn html [res]
  (res/content-type res "text/html; charset=utf-8"))

(def redirect #'res/redirect)
(alter-meta! #'redirect #(merge % (meta #'res/redirect)))