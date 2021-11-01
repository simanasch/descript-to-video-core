(ns descript-to-video.view.main
  (:require [descript-to-video.view.layout :as layout]))

(defn home-view [req]
  (->> [:section.card
        [:h2 "ホーム画面"] ;; ちょっと H1 タグだとうるさいので小さくしました
        [:a {:href "/todo"} "TODO 一覧"]]
       (layout/common req)))