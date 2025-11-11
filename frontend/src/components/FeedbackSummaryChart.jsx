import React from 'react';
import { PieChart, Pie, Cell, Tooltip, Legend } from 'recharts';

const COLORS = ['#4caf50', '#ff9800', '#f44336']; // green, orange, red

export default function FeedbackSummaryChart({ summary }) {
  if (!summary || !summary.sentimentSummary) return <p>No feedback yet</p>;

  // Convert map to array for chart
  const data = [
    { name: 'Positive', value: summary.sentimentSummary.POSITIVE || 0 },
    { name: 'Neutral', value: summary.sentimentSummary.NEUTRAL || 0 },
    { name: 'Negative', value: summary.sentimentSummary.NEGATIVE || 0 },
  ];

  // If all zero, show placeholder text
  if (data.every(d => d.value === 0)) return <p>No feedback yet</p>;

  return (
    <div style={{ width: 250, height: 250, margin: 'auto' }}>
      <PieChart width={250} height={250}>
        <Pie
          data={data}
          dataKey="value"
          nameKey="name"
          cx="50%"
          cy="50%"
          outerRadius={80}
          label
        >
          {data.map((entry, index) => (
            <Cell key={index} fill={COLORS[index % COLORS.length]} />
          ))}
        </Pie>
        <Tooltip />
        <Legend verticalAlign="bottom" height={36} />
      </PieChart>
    </div>
  );
}


