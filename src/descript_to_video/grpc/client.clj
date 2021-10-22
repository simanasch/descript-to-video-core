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
  [EngineName Body Path]
  (-> (. ttsRequest newBuilder)
        (.setLibraryName EngineName)
        (.setBody Body)
        (.setOutputPath Path)
        (.build)))

;; 以下動作確認時のサンプル
(comment
  ;; 接続
  (reset!
   channel
   (-> ManagedChannelBuilder
       (. forAddress "localhost" port)
       (. usePlaintext)
       (. build)))
  (reset!
   stub
   (. TTSServiceGrpc newBlockingStub @channel))

  
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
     (gen-tts-request "さとうささら" "別のさとうささらです" "E://Documents/descript-to-video/output/voices/別のさとうささらです.wav")])
  (def sample-results (map #(. @stub talk %) sample-requests))
  (first sample-results)
  (. @stub talk (first sample-requests))

  (def reply
    (. @stub getSpeechEngineDetail request))
  (def ttsReply
    (. @stub talk talkRequest))
  (def ttsReply
    (. @stub record talkRequest))
  (println reply)
  (println ttsReply)
  (. (. @stub getSpeechEngineDetail request) getEngineName)
  (. (. @stub getSpeechEngineDetail request) getMessage)
  ;; サーバー側を閉じたら以下実行して接続を落としておくこと
  ;; そのままだとプロセスが残る
  (. @channel shutdownNow)
  (. @channel isShutdown))
