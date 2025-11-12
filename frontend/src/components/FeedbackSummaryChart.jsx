import React, { useEffect, useState } from 'react';
import { ResponsiveContainer, PieChart, Pie, Cell, Tooltip, Legend } from 'recharts';

const COLORS = ['#16a34a', '#f59e0b', '#ef4444'];

export default function FeedbackSummaryChart({ summary }) {

  const s = (summary && summary.sentimentSummary) || {};
  const positive = s.POSITIVE ?? s.positive ?? 0;
  const neutral = s.NEUTRAL ?? s.neutral ?? 0;
  const negative = s.NEGATIVE ?? s.negative ?? 0;

  const raw = [
    { name: 'Positive', value: positive },
    { name: 'Neutral', value: neutral },
    { name: 'Negative', value: negative },
  ];

  const data = raw.filter((d) => d.value > 0);
  const total = data.reduce((s, d) => s + d.value, 0);

  const [displayTotal, setDisplayTotal] = useState(0);
  useEffect(() => {
    let raf;
    const duration = 700;
    const start = performance.now();
    const from = 0;
    const to = total;

    const loop = (now) => {
      const t = Math.min(1, (now - start) / duration);
      const value = Math.floor(from + (to - from) * t);
      setDisplayTotal(value);
      if (t < 1) raf = requestAnimationFrame(loop);
    };

    raf = requestAnimationFrame(loop);
    return () => cancelAnimationFrame(raf);
  }, [total]);

  if (data.length === 0 || total === 0) return <p className="text-center text-sm text-gray-500">No feedback yet</p>;

  const tooltipFormatter = (value, name) => {
    const percent = total > 0 ? `${Math.round((value / total) * 100)}%` : '0%';
    return [`${value} (${percent})`, name];
  };

  const legendFormatter = (value, entry) => {
    const val = entry && entry.payload ? entry.payload.value : 0;
    const pct = total > 0 ? `${Math.round((val / total) * 100)}%` : '0%';
    return `${value} (${pct})`;
  };

  return (
    <div className="w-full h-48">
      <ResponsiveContainer>
        <PieChart>
          <Pie
            data={data}
            dataKey="value"
            nameKey="name"
            cx="50%"
            cy="50%"
            outerRadius={70}
            label={false}
            labelLine={false}
            isAnimationActive={true}
            animationDuration={900}
            animationEasing="ease-out"
            startAngle={90}
            endAngle={450}
          >
            {data.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
            ))}
          </Pie>
          {/* Center total count */}
          <text x="50%" y="50%" textAnchor="middle" dominantBaseline="middle" className="text-gray-700" style={{ fontSize: 14, fontWeight: 600 }}>
            {displayTotal}
          </text>
          <Tooltip formatter={tooltipFormatter} />
          <Legend verticalAlign="bottom" height={36} formatter={legendFormatter} />
        </PieChart>
      </ResponsiveContainer>
    </div>
  );
}


