(ns descript-to-video.server
  (:require
   [descript-to-video.core :as core]
   [org.httpkit.server :refer [run-server]]
   [chord.http-kit :refer [with-channel wrap-websocket-handler]]
   [clojure.core.async :refer [<! >! put! close! go chan]]
   [clojure.tools.reader.edn :as edn])
  )
(defonce server (atom nil))
(def port 3000)

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn websocket-handler [{:keys [ws-channel] :as req}]
  (go (let [{:keys [message]} (<! ws-channel)]
        (println "Received message:" message )
        ;; (>! ws-channel "hello client from server!")
        (>! ws-channel (core/start (edn/read-string message)))
        )))

(defn start-server []
  (reset! server 
          (run-server (-> #'websocket-handler wrap-websocket-handler)
                      {:port port})))

(defn restart-server []
  (when @server
    (stop-server)
    (start-server)))

(comment

  (def inst (run-server (-> #'app wrap-websocket-handler) {:port 3000}))
  (start-server)
  @server
  (stop-server)
  (restart-server)
  )
