(ns descript-to-video.aviutl.parser
  (:require
   [clojure.string :as s]
   [yaml.core :as yaml]
   [flatland.ordered.map :refer [ordered-map]]))

(defn- get-level
  "aviutlのオブジェクトについて、ネストの階層を返す"
  [current keys]
  (cond
    (nil? current) nil
    (string? current)
      ;; aviutlのobject:"[{オブジェクトの通し番号}\.{エフェクトの通し番号}]"
    (let [[_ layerSerialNo optionSerialNo] (re-matches #"\[(\w+)\.*(\w+)?\]" current)]
      (cond (nil? layerSerialNo) keys
            (nil? optionSerialNo) (vector (keyword layerSerialNo))
            :else (vector (keyword layerSerialNo) (keyword (str layerSerialNo "." optionSerialNo)))))
    :else keys))

(defn- format-aviutl-line-to-yaml
  "aviutlの.objの行に対して、yamlのタグとして読めるよう整形する"
  [text]
  (->
   text
  ;;  パスのエスケープ
   (s/escape {\" "\\\"", \\ "\\\\"})
  ;;  objectの通し番号の整形
   (s/replace "[" "\"")
   (s/replace "]" "\": ")
  ;;  設定値の整形、値をダブルクォートで囲む
   (s/replace #"=(.*)" ": \"$1\"")))

(defn format-aviutl-object->yaml
  "aviutlの.obj形式のテキストをyamlとして読める形式のテキストにして返す"
  [text]
  (loop [lines (s/split-lines text)
         nest '[]
         result '[]]
    (cond
      (empty? lines) (s/join "\r\n" result)
      :else
      (let [line (first lines)
            lv (get-level line nest)
            indent (cond
                     (or (empty? lv) (empty? nest)) 0
                     (= lv nest) (count lv)
                     :else (count (filter true? (map #(contains? (set nest) %) lv))))]
        (recur
         (rest lines)
         lv
         (conj result (str (s/join "" (repeat indent "  ")) (format-aviutl-line-to-yaml line))))))))

(defn aviutl-object->yaml
  "aviutlの.obj形式のテキストをyamlに変換し,yamlとしてパースした結果のordered-mapを返す"
  [text]
  (yaml/parse-string (format-aviutl-object->yaml text)))

(defn- format-key [symbol]
  (str "[" (name symbol) "]"))

(defn- format-level [key val]
  (str (name key) "=" val))

(defn- get-nested-keys
  "ネストしたmapのkeyをvectorとして返す"
  [mp result]
  (loop [todos (map list (keys mp))
         temp result]
    (let [first-keys (first todos)
          val (get-in mp  first-keys)]
      (cond
        (empty?  todos) temp
        (map? val) (recur
                    (concat (map #(flatten (list first-keys %)) (keys val)) (rest todos))
                    (conj temp first-keys))
        :else (recur
               (rest todos)
               (conj temp first-keys))))))

(defn yaml->aviutl-object
  "yamlのmapをaviutlのobject形式にして返す"
  [mp]
  (letfn [(format-keyval
            [mp keys]
            (let [val (get-in mp keys)]
              (cond (map? val) (format-key (last keys))
                    :else (format-level (last keys) val))))]
    (s/join "\r\n" (reduce #(conj %1 (format-keyval mp %2)) '[] (get-nested-keys mp '[])))))

(defn rename-effect-keys
  [obj key]
  (reduce-kv
   (fn [m k v]
     (let [serial (name key)
           effect-key (name k)]
        ;;  (println k)
       (cond (map? v) (assoc m (keyword (s/replace-first effect-key #"\w+" serial)) v)
             :else (assoc m k v))))
   (ordered-map) obj))

(defn concat-aviutl-map
  "yamlになっているaviutlのmap2つを結合する"
  [base add]
  (letfn [(parseint [x] (cond (int? x) x :else (try (Integer/parseInt x) (catch Exception e 0))))
          (has-layer-and-start [mp] (and (:layer mp) (:start mp)))
          (has-key-layer-and-start [key mp] (and (get-in  mp [key :layer]) (get-in  mp [key :start])))]
    (loop [key 0
          ;;  layer番号、start位置の定義されているオブジェクトのみマージする
           vals-to-add (sort-by
                        (juxt (comp parseint :layer) (comp parseint :start))
                        (filter has-layer-and-start (concat (vals base) (vals add))))
           result (dissoc base (filter (complement #(has-key-layer-and-start % base)) (keys base)))]
      (let [key-for-sorted (keyword (str key))]
        ;; (println "concat-aviutl-map" (count result) (:layer (first vals-to-add)))
        (cond (empty? vals-to-add) result
              :else (recur
                     (inc key)
                     (rest vals-to-add)
                     (assoc result key-for-sorted (rename-effect-keys (first vals-to-add) key-for-sorted))))))))