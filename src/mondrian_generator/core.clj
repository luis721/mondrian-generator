(ns mondrian-generator.core
  (:gen-class))

(defn random-color [options]  (rand-nth options))

(defn is-horizontal-division
  [section]
  (not (:is-horizontal section)))

(defn create-section [top, left, right, bottom, color, level is-horizontal]
  {:top top
   :left left
   :right right
   :bottom bottom
   :color color
   :level level
   :is-horizontal is-horizontal})


(defn create-horizontal-subsections [section division color-generator]
  (let [{:keys [top bottom left right level]} section
        new-level (inc level)
        line  (+ left (* division (- right left)))]
    [(create-section
      top left line bottom (color-generator) new-level true)
     (create-section
      top line right bottom (color-generator) new-level true)]))

(defn create-vertical-subsections [section division color-generator]
  (let [{:keys [top bottom left right level]} section
        new-level (inc level)
        line  (+ top (* division (- bottom top)))]
    [(create-section top left right line (color-generator) new-level false)
     (create-section line left right bottom (color-generator) new-level false)]))


(defn split-section [section division color-generator]
  (if (true? (is-horizontal-division section))
    (create-horizontal-subsections section division color-generator)
    (create-vertical-subsections section division color-generator)))

;; TODO: turn division, max-level and colors into a map
(defn generate-mondrian-r
  [pending-sections sections color-generator division-generator max-level]
  (if (> (count pending-sections) 0)
    (let [current-section (last pending-sections)
          division (division-generator current-section)]
      (if (or
           (= division 1)
           (= division 0)
           (>= (:level current-section) max-level))
        (recur
         (pop pending-sections)
         (conj sections current-section)
         color-generator
         division-generator
         max-level)
        (recur
         (apply conj
                ; delete current section before adding the child sections
                (pop pending-sections)
                ; new subsections
                (split-section current-section division color-generator))
         sections
         color-generator
         division-generator
         max-level)))
    sections))


(defn generate-mondrian [config division-generator color-generator max-level]
  (let [pending-sections
        (create-section 0 0 (:max-x config) (:max-y config) (color-generator) 0 false)]
    (generate-mondrian-r [pending-sections] [] color-generator division-generator max-level)))


(defn division-generator [section min-level]
  (let [{level :level} section]
    (if (< level min-level)
      (rand-nth [0.3 0.4 0.5 0.6 0.7])
      (rand-nth [0 0.3 0.4 0.5 0.6 0.7 1]))))

(defn main []
  (generate-mondrian
   {:max-x 1000 :max-y 1000}
   (fn [section] (division-generator section 3))
   (fn [] (random-color ["red" "blue" "yellow" "white" "black"]))
   5))