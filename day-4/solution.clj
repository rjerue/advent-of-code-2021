#!/usr/bin/env bb
(require '[clojure.string :as str])

(def base
  (->> (or *command-line-args* ["example.txt"])
       (first)
       (slurp)
       (str/split-lines)))

(def inputs (-> base first (str/split #",")))

(defn row->list
  [row]
  (->> row (re-seq #".{1,3}") (map str/trim)))

(def boards
  (->> base (rest)
       (partition-by str/blank?)
       (filter #(not (= '("") %)))
       (map #(map row->list %))))

(defn finder 
  [input board]
  (loop [x 0
         y 0
         row (first board)]
    (cond
      (nil? row) nil
      (>= x (count board)) nil
      (>= y (count row)) (let [bump (inc x)]
                           (recur bump 0 (nth board bump nil)))
      (= input (-> board (nth x) (nth y))) (vector x y)
      :else
      (recur x (inc y) row))))

(defn bingo-check
  [answers]
  (->> answers (sort-by first) (group-by first) (vals) (map count) (some #(= 5 %))))

(defn bingo-exists?
  [answers]
  (or (bingo-check answers)
      (bingo-check (map reverse answers))))

(defn bingo-solution
  [board answers]
  (let [indexes (set (map #(+ (* 5 (first %)) (second %)) answers))
        numbers (reduce into [] board)
        remaining (->>
                   (map-indexed vector numbers)
                   (filter #(not (contains? indexes (first %))))
                   (map second)
                   (map #(Integer/parseInt %)))]
    (apply + remaining)))

(defn bingo
  [choose-last? game-boards]
  (loop [win-boards #{}
         answer-index 0
         board-index 0
         answers-set {}]
    (if-let [current-board (nth game-boards board-index nil)]
      (if (contains? win-boards board-index)
        (recur win-boards answer-index (inc board-index) answers-set)
        (let [target (nth inputs answer-index nil)
              found (finder target current-board)
              answers (cond-> (get answers-set board-index []) (some? found) (conj found))
              bingo? (bingo-exists? (filter some? answers))]
          (if bingo?
            (if (and choose-last? (< 1 (- (count game-boards) (count win-boards))))
              (recur
               (conj win-boards board-index)
               answer-index
               board-index
               (assoc answers-set board-index answers))
              (* (Integer/parseInt target) (bingo-solution current-board answers)))
            (recur win-boards answer-index (inc board-index) (assoc answers-set board-index answers)))))
      (recur win-boards (inc answer-index) 0 answers-set))))

(println "Part 1" (bingo false boards))
(println "Part 2" (bingo true boards))
