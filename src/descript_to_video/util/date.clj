(ns descript-to-video.util.date)

(def date-format (java.text.SimpleDateFormat. "YYMMddHHmmssSSS"))

(defn get-time-stamp
  []
  (.format date-format (new java.util.Date)))