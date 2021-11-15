(ns descript-to-video.grpc.client
  (:gen-class
   :name descript_to_video.grpc-client.service.TTSServiceInpl
   :extends
   io.grpc.ttscontroller.TTSServiceGrpc$TTSServiceImplBase)
  (:import
   [io.grpc.ttscontroller TTSServiceGrpc SpeechEngineRequest SpeechEngineList ttsRequest ttsResult]
   [io.grpc ManagedChannelBuilder]))


(def port 5001)

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

(defn format-result
  [result]
  {:isSuccess (.getIsSuccess result)
   :libraryName (.getLibraryName result)
   :engineName (.getEngineName result)
   :Body (.getBody result)
   :outputPath (.getOutputPath result)})

(defn talk
  ([request]
  (let [result (. @stub talk request)]
    (format-result result)))
  ([LibraryName Body Path]
   (talk (gen-tts-request LibraryName Body Path)))
  ([LibraryName Body]
   (talk LibraryName Body "")))

(defn record
  ([request]
   (let [result (. @stub record request)]
    (format-result result)))
  ([LibraryName Body Path]
   (record (gen-tts-request LibraryName Body Path))))