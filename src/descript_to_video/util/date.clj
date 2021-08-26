(ns descript-to-video.util.date)

(def date-format (java.text.SimpleDateFormat. "YYMMddHHmmssSSS"))

(defn getTimeStamp
  []
  (.format date-format (new java.util.Date)))