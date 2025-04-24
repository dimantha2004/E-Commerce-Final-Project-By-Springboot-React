import React, { useEffect, useRef } from "react";
import { Bar } from "react-chartjs-2";
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from "chart.js";

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const BarChart = ({ data, options }) => {
    const chartRef = useRef(null);

    useEffect(() => {
        return () => {
            
            if (chartRef.current) {
                chartRef.current.destroy();
            }
        };
    }, []);

    return <Bar ref={chartRef} data={data} options={options} />;
};

export default BarChart;