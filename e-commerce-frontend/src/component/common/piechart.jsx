import React, { useEffect, useRef } from "react";
import { Pie } from "react-chartjs-2";
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from "chart.js";

// Register Chart.js components
ChartJS.register(ArcElement, Tooltip, Legend);

const PieChart = ({ data, options }) => {
    const chartRef = useRef(null);

    useEffect(() => {
        return () => {
            // Cleanup chart instance on unmount
            if (chartRef.current) {
                chartRef.current.destroy();
            }
        };
    }, []);

    return <Pie ref={chartRef} data={data} options={options} />;
};

export default PieChart;