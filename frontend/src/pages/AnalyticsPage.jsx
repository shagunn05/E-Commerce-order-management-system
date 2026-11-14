import React, { useEffect, useState } from 'react';
import {
  BarChart3,
  Boxes,
  CircleDollarSign,
  Crown,
  DollarSign,
  PieChart as PieIcon,
  ShoppingBag,
  TrendingUp,
  Users,
} from 'lucide-react';
import StatCard from '../components/StatCard';
import {
  CategoryValuationBarChart,
  CustomerTiersBarChart,
  OrderStatusPieChart,
  RevenueTrendChart,
  TopProductsBarChart,
} from '../components/Charts';
import LoadingSpinner from '../components/LoadingSpinner';
import analyticsService from '../services/analyticsService';
import { formatCurrency, formatNumber, formatPercent } from '../utils/format';
import { useToast } from '../context/ToastContext';
import TierBadge from '../components/TierBadge';

export default function AnalyticsPage() {
  const [data, setData] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const { error } = useToast();

  useEffect(() => {
    const fetchAnalytics = async () => {
      try {
        setIsLoading(true);
        const overview = await analyticsService.getOverview();
        setData(overview);
      } catch (err) {
        error(err.message, 'Failed to fetch analytics data');
      } finally {
        setIsLoading(false);
      }
    };

    fetchAnalytics();
  }, []);

  if (isLoading || !data) {
    return <LoadingSpinner message="Calculating business intelligence..." fullHeight />;
  }

  const {
    totals,
    revenueTrend,
    statusBreakdown,
    topProducts,
    inventoryByCategory,
    tierBreakdown,
    topCustomers,
    valuation,
  } = data;

  return (
    <div className="page-container analytics-page">
      {/* Page Header */}
      <div className="page-header">
        <div>
          <h2 className="page-title">Business Analytics & Intelligence</h2>
          <p className="page-subtitle">
            Revenue trends, inventory valuation, order size distribution, and top tier performance.
          </p>
        </div>
      </div>

      {/* Top Level Metric StatCards */}
      <div className="stats-grid">
        <StatCard
          title="Gross Valuation"
          value={formatCurrency(valuation.sum)}
          subtitle={`Across ${valuation.count} total placed orders`}
          icon={CircleDollarSign}
          colorScheme="blue"
        />

        <StatCard
          title="Average Order Value"
          value={formatCurrency(totals.averageOrder)}
          subtitle="AOV for active fulfilled orders"
          icon={TrendingUp}
          colorScheme="navy"
        />

        <StatCard
          title="Highest Order Value"
          value={formatCurrency(valuation.max)}
          subtitle={`Minimum: ${formatCurrency(valuation.min)}`}
          icon={ShoppingBag}
          colorScheme="emerald"
        />

        <StatCard
          title="Inventory Assets"
          value={formatCurrency(
            inventoryByCategory.reduce((sum, cat) => sum + cat.value, 0),
          )}
          subtitle={`Across ${inventoryByCategory.length} merchandise categories`}
          icon={Boxes}
          colorScheme="purple"
        />
      </div>

      {/* Primary Chart: Revenue Trajectory */}
      <div className="dashboard-card mb-6">
        <div className="dashboard-card-header">
          <div>
            <h3 className="card-title">6-Month Revenue & Volume Trajectory</h3>
            <p className="card-subtitle">Monthly sales volume computed using Java Streams aggregations</p>
          </div>
        </div>
        <div className="card-body">
          <RevenueTrendChart data={revenueTrend} height={320} />
        </div>
      </div>

      {/* Two Column Grid: Status Breakdown & Category Valuation */}
      <div className="charts-grid-two-column mb-6">
        {/* Order Status Distribution */}
        <div className="dashboard-card chart-card">
          <div className="dashboard-card-header">
            <div>
              <h3 className="card-title">Order Status Share</h3>
              <p className="card-subtitle">Volume distribution across order lifecycle states</p>
            </div>
          </div>
          <div className="card-body">
            <OrderStatusPieChart data={statusBreakdown} height={280} />
          </div>
        </div>

        {/* Inventory Valuation by Category */}
        <div className="dashboard-card chart-card">
          <div className="dashboard-card-header">
            <div>
              <h3 className="card-title">Inventory Valuation by Category</h3>
              <p className="card-subtitle">Stock quantity multiplied by unit catalog price</p>
            </div>
          </div>
          <div className="card-body">
            <CategoryValuationBarChart data={inventoryByCategory} height={280} />
          </div>
        </div>
      </div>

      {/* Two Column Grid: Top Products Sold & Tier Demographics */}
      <div className="charts-grid-two-column mb-6">
        {/* Top Products Horizontal Bar Chart */}
        <div className="dashboard-card chart-card">
          <div className="dashboard-card-header">
            <div>
              <h3 className="card-title">Product Performance</h3>
              <p className="card-subtitle">Top products ranked by total units dispatched</p>
            </div>
          </div>
          <div className="card-body">
            <TopProductsBarChart data={topProducts} height={280} />
          </div>
        </div>

        {/* Customer Loyalty Tier Distribution */}
        <div className="dashboard-card chart-card">
          <div className="dashboard-card-header">
            <div>
              <h3 className="card-title">Customer Loyalty Tier Distribution</h3>
              <p className="card-subtitle">Active accounts across Regular, Silver, Gold, Platinum</p>
            </div>
          </div>
          <div className="card-body">
            <CustomerTiersBarChart data={tierBreakdown} height={280} />
          </div>
        </div>
      </div>

      {/* Top Spending Customers Leaderboard */}
      <div className="dashboard-card">
        <div className="dashboard-card-header">
          <div>
            <h3 className="card-title">Top Spending Customers Leaderboard</h3>
            <p className="card-subtitle">High-value clients driving store volume</p>
          </div>
        </div>

        <div className="card-body p-0">
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Rank</th>
                  <th>Customer Name</th>
                  <th>Tier</th>
                  <th style={{ textAlign: 'right' }}>Cumulative Spend</th>
                  <th style={{ textAlign: 'right' }}>Share of Revenue</th>
                </tr>
              </thead>
              <tbody>
                {topCustomers.map((cust, idx) => {
                  const share = totals.revenue ? (cust.total / totals.revenue) * 100 : 0;
                  return (
                    <tr key={cust.name}>
                      <td className="strong">#{idx + 1}</td>
                      <td className="strong">{cust.name}</td>
                      <td>
                        <TierBadge tier={cust.tier} size="sm" />
                      </td>
                      <td style={{ textAlign: 'right' }} className="strong text-primary">
                        {formatCurrency(cust.total)}
                      </td>
                      <td style={{ textAlign: 'right' }}>{formatPercent(share)}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
