import React from "react";
import { Pie } from "@ant-design/plots";

const StatusPieChart = ({ data, colorMap }) => {
  const config = {
    data,
    angleField: "value",
    colorField: "name",
    color: colorMap ? (d) => colorMap[d.name] || undefined : undefined,
    innerRadius: 0.6,
    legend: {
      color: {
        position: "bottom",
      },
    },
  };

  return <Pie {...config} />;
};

export default StatusPieChart;
