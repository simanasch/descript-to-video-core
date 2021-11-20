(ns descript-to-video.core
  (:gen-class)
  (:require
  ;;  [compojure.core :refer [routes]]
  ;;  [ring.adapter.jetty :as server]
  ;; [descript-to-video.handler.main :refer [main-routes]]
   [org.httpkit.server :refer [run-server]]
   [chord.http-kit :refer [wrap-websocket-handler]]
   [descript-to-video.grpc.client :as client]
   [descript-to-video.handler.websocket :as websocket-server]
  ;;  [descript-to-video.middleware :refer [wrap-dev]]
   ))
  ;; [descript-to-video.grpc.service]
  ;; (:import [io.grpc Server ServerBuilder]
  ;;          [io.grpc.stub StreamObserver]
  ;;          [descript-to-video.grpc.service GreeterServiceImpl]))
(defonce server (atom nil))
(def port 3000)

(def app
  (-> #'websocket-server/websocket-handler 
      wrap-websocket-handler))

(defn start-server []
  (reset! server 
          (run-server app {:port port})))

(defn stop-server []
  (when-not (nil? @server)
    (@server)
    (reset! server nil)))

(defn restart-server []
  (when @server
    (stop-server)
    (start-server)))


(defn -main
  [& args]
  ;; TODO:引数の値による処理分岐
  (apply println "Received args:" args)
  (start-server)
  ;; (client/shutdown-connection!)
  )