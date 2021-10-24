(ns descript-to-video.grpc.client
  (:gen-class
   :name descript-to-video.grpc-client.service.TTSServiceInpl
   :extends
   io.grpc.ttscontroller.TTSServiceGrpc$TTSServiceImplBase)
  (:import
   [io.grpc.ttscontroller TTSServiceGrpc SpeechEngineRequest SpeechEngineList ttsRequest ttsResult]
   [io.grpc ManagedChannelBuilder]))


(def port 30051)

(def channel
  (atom
   (. (. (. ManagedChannelBuilder forAddress "localhost" port) usePlaintext) build)))

(def stub
  (atom
   (. TTSServiceGrpc newBlockingStub @channel)))

(defn gen-tts-request
  ([LibraryName Body Path]
  (-> (. ttsRequest newBuilder)
      (.setLibraryName LibraryName)
      (.setBody Body)
      (.setOutputPath Path)
      (.build)))
  ([EngineName LibraryName Body Path]
  (-> (. ttsRequest newBuilder)
      (.setEngineName EngineName)
      (.setLibraryName LibraryName)
      (.setBody Body)
      (.setOutputPath Path)
      (.build))))

(defn reset-connection!
  []
  (reset!
   channel
   (-> ManagedChannelBuilder
       (. forAddress "localhost" port)
       (. usePlaintext)
       (. build)))
  (reset!
   stub
   (. TTSServiceGrpc newBlockingStub @channel)))

(defn shutdown-connection!
  []
  (. @channel shutdownNow)
  (. @channel isShutdown))

(defn talk
  [request]
  (let [result (. @stub talk request)]
    (.getIsSuccess result)))

(defn record
  [request]
  (let [result (. @stub record request)]
    (.getOutputPath result)))

;; 以下動作確認時のサンプル
(comment
  ;; 接続
  (reset-connection!)
  (def request
    (-> SpeechEngineRequest
        (. newBuilder)
        (.setEngineName "葵")
        (.build)))
  (def talkRequest
    (-> (. ttsRequest newBuilder)
        (.setLibraryName "葵")
        (.setBody "別の葵ちゃんだよ")
        (.setOutputPath "E://Documents/descript-to-video/output/voices/別の葵ちゃんだよ.wav")
        (.build)))
  (def recordRequest
    (-> (. ttsRequest newBuilder)
        (.setLibraryName "葵")
        (.setBody "別の葵ちゃんだよ")
        (.setOutputPath "E://Documents/descript-to-video/output/voices/別の葵ちゃんだよ.wav")
        (.build)))
  (def sample-requests
    [(gen-tts-request "ゆかり" "ゆかりさんです" "E://Documents/descript-to-video/output/voices/ゆかりさんです.wav")
     (gen-tts-request "さとうささら" "さとうささらです" "E://Documents/descript-to-video/output/voices/さとうささらです.wav")
     (gen-tts-request "葵" "葵ちゃんだよ" "E://Documents/descript-to-video/output/voices/葵ちゃんだよ.wav")
     (gen-tts-request "AIVOICE" "雫" "私もゆかりだよ" "E://Documents/descript-to-video/output/voices/ゆかりさんです.wav")
     (gen-tts-request "さとうささら" "別のさとうささらです" "E://Documents/descript-to-video/output/voices/別のさとうささらです.wav")
     (gen-tts-request "VOICEROID64" "ゆかり"  "Voiceroidのゆかりさんです" "E://Documents/descript-to-video/output/voices/ゆかりさんです.wav")])
  (map #(talk %) sample-requests)
  (map #(record %) sample-requests)
  (first sample-results)
  (record (gen-tts-request "ゆかり" "これはテストです" "./これはテストです"))
  (talk (last sample-requests))
  (. @stub talk (first sample-requests))

  (def reply
    (. @stub getSpeechEngineDetail request))
  (def ttsReply
    (. @stub talk talkRequest))
  (map (comp  #(.getEngineName %)) (.getDetailItemList reply))
  (def record-output-path (record talkRequest))
  (println reply)
  (println ttsReply)
  (. (. @stub getSpeechEngineDetail request) getEngineName)
  (. (. @stub getSpeechEngineDetail request) getMessage)
  ;; サーバー側を閉じたら以下実行して接続を落としておくこと
  ;; そのままだとプロセスが残る
  (shutdown-connection!))
