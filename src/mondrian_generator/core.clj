(ns mondrian-generator.core
  (:gen-class))

(defn random-color [options]  (rand-nth options))

(defn random-division [options] (rand-nth options))

(defn is-horizontal-division [] (rand-nth [true false]))

(defn random-max-level [options] (rand-nth options))

(defn create-section [top, left, right, bottom, color, level]
  {:top top
   :left left
   :right right
   :bottom bottom
   :color color
   :level level})


(defn create-horizontal-subsections [section division color-generator]
  (let [{:keys [top bottom left right level]} section
        new-level (inc level)
        line  (+ left (* division (- right left)))]
    [(create-section
      top left line bottom (color-generator) new-level)
     (create-section
      top line right bottom (color-generator) new-level)]))

(defn create-vertical-subsections [section division color-generator]
  (let [{:keys [top bottom left right level]} section
        new-level (inc level)
        line  (+ top (* division (- bottom top)))]
    [(create-section top left right line (color-generator) new-level)
     (create-section line left right bottom (color-generator) new-level)]))


(defn split-section [section division color-generator]
  (if (true? (is-horizontal-division))
    (create-horizontal-subsections section division color-generator)
    (create-vertical-subsections section division color-generator)))

;; TODO: turn division, max-level and colors into a map
(defn generate-mondrian-r
  [pending-sections sections color-generator divisions max-level]
  (if (> (count pending-sections) 0)
    (let [current-section (last pending-sections)
          division (random-division divisions)]
      (if (or
           (= division 1)
           (= division 0)
           (>= (current-section :level) max-level))
        (recur
         (pop pending-sections)
         (conj sections current-section)
         color-generator
         divisions
         max-level)
        (recur
         (apply conj
                ; delete current section before adding the child sections
                (pop pending-sections)
                ; new subsections
                (split-section current-section division color-generator))
         sections
         color-generator
         divisions
         max-level)))
    sections))


(defn generate-mondrian [config divisions color-generator max-level]
  (let [pending-sections
        (create-section 0 0 (config :max-x) (config :max-y) (color-generator) 0)]
    (generate-mondrian-r [pending-sections] [] color-generator divisions max-level))) ;; todo queue etc


(defn main []
  (generate-mondrian
   {:max-x 1000 :max-y 1000}
   [0 0.25 0.5 0.5 0.5 0.75 1]
   (fn [] (random-color ["red" "blue" "yellow" "white" "black"]))
   5))


(main)