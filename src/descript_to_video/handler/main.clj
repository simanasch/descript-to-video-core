(ns descript-to-video.handler.main
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [descript-to-video.util.response :as res]
            [descript-to-video.view.main :as view]))

(defn home [req]
  (-> (view/home-view req)
      res/response
      res/html))

(defroutes main-routes
  (GET "/" _ home)
  (route/not-found "<h1>Not found</h1>"))