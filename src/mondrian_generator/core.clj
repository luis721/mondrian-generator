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


(defn create-horizontal-subsections [section division colors]
  (let [{:keys [top bottom left right level]} section
        new-level (inc level)
        line  (+ left (* division (- right left)))]
    [(create-section
      top left line bottom (random-color colors) new-level)
     (create-section
      top line right bottom (random-color colors) new-level)]))

(defn create-vertical-subsections [section division colors]
  (let [{:keys [top bottom left right level]} section
        new-level (inc level)
        line  (+ top (* division (- bottom top)))]
    [(create-section top left right line (random-color colors) new-level)
     (create-section line left right bottom (random-color colors) new-level)]))


(defn split-section [section division colors]
  (if (true? (is-horizontal-division))
    (create-horizontal-subsections section division colors)
    (create-vertical-subsections section division colors)))

;; TODO: turn division, max-level and colors into a map
;; collors will be a function instead of the list of options
(defn generate-mondrian-r
  [pending-sections sections colors divisions max-level]
  (if (> (count pending-sections) 0)
    (let [current-section (last pending-sections)
          division (random-division divisions)]
      (if (or
           (= division 1)
           (= division 0)
           (>= (current-section :level) max-level))
        (generate-mondrian-r
         (pop pending-sections)
         (conj sections current-section)
         colors
         divisions
         max-level)
        (generate-mondrian-r
         (apply conj
                pending-sections
                (split-section current-section division colors))
         sections
         colors
         divisions
         max-level)))
    sections))


(defn generate-mondrian [config divisions colors max-level]
  (let [pending-sections
        (create-section 0 0 (config :max-x) (config :max-y) (random-color colors) 0)]
    (generate-mondrian-r [pending-sections] [] colors divisions max-level))) ;; todo queue etc


(defn main []
  (generate-mondrian
   {:max-x 1000 :max-y 1000}
   [0 0.25 0.5 0.75 1]
   ["red" "blue" "yellow" "white" "black"]
   5))

(main)