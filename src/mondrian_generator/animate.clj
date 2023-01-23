(ns mondrian-generator.animate
  (:require [quil.core :as q]
            [mondrian-generator.core :as core]
            [mondrian-generator.draw :as draw]
            [quil.middleware :as m]))

(defn setup []
  (q/background 255)
  (q/frame-rate 1)
  {:data (core/generate-mondrian
          {:max-x 1500 :max-y 1000}
          (fn [section] (core/division-generator section 2))
         ;; TODO: color generator will work in function of the contiguos section
         ;; prefer some color combinations over others
          (fn [] (core/random-color
                 ;; TODO: random colors given weight
                  [:red :blue :yellow :white :white :white :blue :yellow :white :white]))
          4)
   :shown []
   :index 0})

(defn draw [state]
  (let [{:keys [data index]} state
        {:keys [sections divisions]} data]
    (q/stroke-weight 5)
    (q/background 255)
    (loop [i 0]
      (when (< i (min (count divisions) index))
        (apply q/line (divisions i))
        (recur (inc i))))

    (when (<= (count divisions) index)
      (let [count (- index (count sections))]
        (loop [i 0]
          (when (< i count)
            (draw/draw-section (sections i))
            (recur (inc i))))))
    ;(q/save-frame (str "frames/frame-" index ".png"))
    (when (>= index (+ (count divisions) (count sections)))
      (q/exit))))


(defn update-state [state]
  (let [{:keys [data index]} state]
    {:data data
     :index (inc index)}))

(q/defsketch animate
  :host "host"
  :setup setup
  :title "Mondrianesque"
  :size [1500 1000]
  :update update-state
  :draw draw
  :middleware [m/fun-mode])

