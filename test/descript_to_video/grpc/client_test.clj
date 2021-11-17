(ns descript-to-video.grpc.client-test
  (:require [clojure.test :refer :all]
            [descript-to-video.grpc.client :refer :all])
  (:import
   [io.grpc.ttscontroller TTSServiceGrpc SpeechEngineRequest SpeechEngineList ttsRequest ttsResult]
   [io.grpc ManagedChannelBuilder]))

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
        (.setOutputPath "./output/voices/別の葵ちゃんだよ.wav")
        (.build)))
  (def recordRequest
    (-> (. ttsRequest newBuilder)
        (.setLibraryName "葵")
        (.setBody "別の葵ちゃんだよ")
        (.setOutputPath "./output/voices/別の葵ちゃんだよ.wav")
        (.build)))
  (def sample-requests
    [(gen-tts-request "ゆかり" "ゆかりさんです" "./output/voices/ゆかりさんです.wav")
     (gen-tts-request "さとうささら" "さとうささらです" "./output/voices/さとうささらです.wav")
     (gen-tts-request "葵" "葵ちゃんだよ" "./output/voices/葵ちゃんだよ.wav")
     (gen-tts-request "AIVOICE" "雫" "私もゆかりだよ" "./output/voices/ゆかりさんです.wav")
     (gen-tts-request "さとうささら" "別のさとうささらです" "./output/voices/別のさとうささらです.wav")
     (gen-tts-request "VOICEROID64" "ゆかり"  "Voiceroidのゆかりさんです" "./output/voices/ゆかりさんです.wav")])
  (map #(talk %) sample-requests)
  (map #(record %) sample-requests)
  (record (gen-tts-request "ゆかり" "これはテストです" "./これはテストです"))
  (talk (last sample-requests))
  (. @stub talk (first sample-requests))

  (def reply
    (. @stub getSpeechEngineDetail request))
  (def ttsReply
    (. @stub talk talkRequest))
  (map (comp  #(.getEngineName %)) (.getDetailItemList reply))
  (def record-output-path (record talkRequest))
  ;; サーバー側を閉じたら以下実行して接続を落としておくこと
  ;; そのままだとプロセスが残る
  (shutdown-connection!))