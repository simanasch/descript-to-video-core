(ns descript-to-video.aviutl.parser
  (:require
   [clojure.string :as s]
   [yaml.core :as yaml]
   [descript-to-video.util.map :as m])
  (:refer  flatland.ordered.map))

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

(defn- get-keyval
  [st]
  (cond (not (string? st)) nil
        :else (let [[key val] (s/split st #"=")]
                (println key val)
                {(keyword key) (str val)})))

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

(defn- compare-by-layer-start
  "aviutlからyamlにparseしたmapについて、layerの昇順-start位置での昇順-引数順の比較を行うcomparater"
    [x y]
    (letfn [(parseInt [v] (cond (int? v) v :else (try (Integer/parseInt v) (catch Exception e nil))))]
      (println (:layer x) (:layer y))
      ;; (println (:start x) (:start y))
      (cond
        (> (parseInt (:layer x)) (parseInt (:layer y))) y
        (> (parseInt (:start x)) (parseInt (:start y))) y
        :else x)))

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

(defn conj-aviutl-map
  "yamlになっているaviutlのmap2つを結合する"
  [base add]
  (loop [key (inc (Integer/parseInt (name (last (keys base)))))
         keys-to-add (keys add)
         result base]
    (println key keys-to-add)
    (cond (empty? keys-to-add) result
          :else
          (recur
           (inc key)
           (rest keys-to-add)
          ;;  オブジェクトの通し番号とオプションに含まれる通し番号が一致しないので要成形
           (assoc result (keyword (str key)) (get add (first keys-to-add)))))))

(defn rename-effect-keys
  [obj key]
  (reduce-kv
   (fn [m k v]
     (let [serial (name key)
           effect-key (name k)]
        ;;  (println k)
       (cond (map? v) (assoc m (keyword (s/replace-first effect-key #"\w+?" serial)) v)
             :else (assoc m k v))))
   (ordered-map) obj))

(defn sort-aviutl-object-map
  "aviutlのobjectのmapについて、layerの昇順-startの昇順でソートする"
  [aviutl-object-map]
  (letfn [(sort-by-layer-start
            [x y]
            (let [layer-x (get-in aviutl-object-map [x :layer])
                  layer-y (get-in aviutl-object-map [y :layer])]
              ;; (println "comparing:" x y layer-x layer-y)
              (cond (= layer-x layer-y) (compare (get-in aviutl-object-map [x :start]) (get-in aviutl-object-map [y :start]))
                    :else (compare (get-in aviutl-object-map [x :layer]) (get-in aviutl-object-map [y :layer])))))]
    (loop [keys (sort
                 sort-by-layer-start
                 (keys aviutl-object-map))
           object-serial-number 0
           result (m/map->ordered-map {:exedit (:exedit aviutl-object-map)})]
      (let [key (first keys)
            key-serial-number (try (Integer/parseInt (name key)) (catch Exception e nil))
            key-for-sorted (keyword (str object-serial-number))]
        ;; (println key)
        (cond (empty? keys) result
              (nil? key-serial-number) (recur (rest keys) object-serial-number (assoc result key (get aviutl-object-map key)))
              :else (recur (rest keys) (inc object-serial-number) (assoc result key-for-sorted (rename-effect-keys (get aviutl-object-map key) key-for-sorted))))))))

(comment
  ;; 動作確認に使っているスニペット系
  ;; TODO:テストに移す
  (def sample-map (aviutl-object->yaml (slurp "./sample/sample.exo" :encoding "shift-jis")))
  (def sample-tts-object (descript-to-video.aviutl.aviutl/get-tts-object 1  "./output/voices/210923210356664_ゆかり_おっつおっつ.wav" "おっつおっつ" "ゆかり"))
  (def sample-tts-merged (conj-aviutl-map sample-map sample-tts-object))
  (->>
   sample-tts-merged
   yaml->aviutl-object
   (spit "../tmp.exo"))
  
  (def sample-slide1 (aviutl-object->yaml (slurp "./sample/slide_template.exo" :encoding "shift-jis")))
  (def sample-slide2 (aviutl-object->yaml (slurp "./output/slide2.exo" :encoding "shift-jis")))
  (def added (conj-aviutl-map sample-map sample-slide1))
  ;; sample-yamlとparsedは一致する(はず)
  (def raw-dest (slurp "./sample/sample_dest.exo" :encoding "shift-jis"))
  (def sample-yaml (yaml/from-file "./sample/sample.yaml"))
  (def parsed2 (aviutl-object->yaml raw-dest))

  (map list (keys sample-map))
  (map #(list :exedit %) (keys (get-in sample-map '(:exedit))))
  (map #(get-in sample-yaml %) (map #(list :exedit %) (keys (get-in sample-map '(:exedit)))))
  (def sorted-added (sort-aviutl-object-map added))
  (keys (:2 sorted-added))
  (keys (rename-effect-keys (:2 sorted-added) :2))
  (:exedit sorted-added)
  (keys sorted-added)
  (get-in sorted-added [:0 :layer])
  (get-in sorted-added [:1 :layer])
  (get-in sorted-added [:2 :layer])

  
  (get-in sample-map [:0 :layer])
  (get-in sample-tts-object [:0 :layer])
  (->
   sample-tts-object
  ;;  sample-map
  ;;  (get-in [:0 :start])
   (get-in [:0 :layer])
   Integer/parseInt)
  (get-in sample-tts-object [:0 :start])
  
  (> 28 10)
  (:layer (compare-by-layer-start (get-in sample-map [:0]) (get-in sample-tts-object [:0])))
  (< 1 2)

  (sort-aviutl-object-map added)
  (keyword "0")

  (spit "./output/sorted.exo" (yaml->aviutl-object added) :encoding "shift-jis")
  (keys sorted))