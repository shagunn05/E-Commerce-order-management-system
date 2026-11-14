import React, { useEffect, useMemo, useState } from 'react';
import {
  AlertTriangle,
  Download,
  Edit2,
  Filter,
  Grid,
  List,
  Package,
  Plus,
  RotateCcw,
  Search,
  Sparkles,
  Trash2,
  Weight,
} from 'lucide-react';
import SearchBar from '../components/SearchBar';
import FilterDropdown from '../components/FilterDropdown';
import StatusBadge from '../components/StatusBadge';
import ProductCard from '../components/ProductCard';
import DataTable from '../components/DataTable';
import Modal from '../components/Modal';
import LoadingSpinner from '../components/LoadingSpinner';
import EmptyState from '../components/EmptyState';
import productsService from '../services/productsService';
import { formatCurrency, formatDate } from '../utils/format';
import { useToast } from '../context/ToastContext';
import { useSettings } from '../context/SettingsContext';

const INITIAL_FORM = {
  sku: '',
  name: '',
  category: 'Electronics',
  price: '',
  stockQuantity: '',
  productType: 'PHYSICAL',
  weightKg: '0.5',
  downloadUrl: 'https://downloads.apexcommerce.com/asset',
  billingPeriodMonths: '1',
};

export default function ProductsPage() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [viewMode, setViewMode] = useState('grid'); // 'grid' | 'table'

  // Filters
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('ALL');
  const [selectedType, setSelectedType] = useState('ALL');
  const [stockFilter, setStockFilter] = useState('ALL'); // 'ALL' | 'LOW' | 'OUT'

  // Modals
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [isStockModalOpen, setIsStockModalOpen] = useState(false);

  const [formData, setFormData] = useState(INITIAL_FORM);
  const [activeProduct, setActiveProduct] = useState(null);
  const [stockInput, setStockInput] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const { settings } = useSettings();
  const { success, error, warning } = useToast();

  const loadProducts = async () => {
    try {
      setIsLoading(true);
      const [allProducts, distinctCats] = await Promise.all([
        productsService.getAll(),
        productsService.getCategories(),
      ]);
      setProducts(allProducts);
      setCategories(distinctCats);
    } catch (err) {
      error(err.message, 'Failed to fetch catalog');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadProducts();
  }, []);

  // Filtered Products Memo
  const filteredProducts = useMemo(() => {
    return products.filter((p) => {
      // Search
      const matchesSearch =
        !searchQuery ||
        p.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        p.sku.toLowerCase().includes(searchQuery.toLowerCase());

      // Category
      const matchesCategory = selectedCategory === 'ALL' || p.category === selectedCategory;

      // Type
      const matchesType = selectedType === 'ALL' || p.productType === selectedType;

      // Stock
      let matchesStock = true;
      if (stockFilter === 'LOW') {
        matchesStock = p.stockQuantity <= (settings.lowStockThreshold || 10) && p.stockQuantity > 0;
      } else if (stockFilter === 'OUT') {
        matchesStock = p.stockQuantity === 0;
      }

      return matchesSearch && matchesCategory && matchesType && matchesStock;
    });
  }, [products, searchQuery, selectedCategory, selectedType, stockFilter, settings.lowStockThreshold]);

  // Open Edit Modal
  const handleOpenEdit = (product) => {
    setActiveProduct(product);
    setFormData({
      sku: product.sku,
      name: product.name,
      category: product.category,
      price: String(product.price),
      stockQuantity: String(product.stockQuantity),
      productType: product.productType,
      weightKg: String(product.weightKg || '0.5'),
      downloadUrl: product.downloadUrl || '',
      billingPeriodMonths: String(product.billingPeriodMonths || '1'),
    });
    setIsEditModalOpen(true);
  };

  // Open Stock Modal
  const handleOpenStock = (product) => {
    setActiveProduct(product);
    setStockInput(String(product.stockQuantity));
    setIsStockModalOpen(true);
  };

  // Open Delete Modal
  const handleOpenDelete = (product) => {
    setActiveProduct(product);
    setIsDeleteModalOpen(true);
  };

  // Submit Add Product
  const handleCreateProduct = async (e) => {
    e.preventDefault();
    if (!formData.sku || !formData.name || !formData.price) {
      warning('Please fill in all mandatory product fields.', 'Validation Error');
      return;
    }

    try {
      setIsSubmitting(true);
      const created = await productsService.create(formData);
      success(`Product "${created.name}" created successfully.`, 'Product Added');
      setIsAddModalOpen(false);
      setFormData(INITIAL_FORM);
      await loadProducts();
    } catch (err) {
      error(err.message, 'Create Failed');
    } finally {
      setIsSubmitting(false);
    }
  };

  // Submit Edit Product
  const handleUpdateProduct = async (e) => {
    e.preventDefault();
    if (!activeProduct) return;

    try {
      setIsSubmitting(true);
      await productsService.update(activeProduct.id, formData);
      success(`Product "${formData.name}" updated successfully.`, 'Product Updated');
      setIsEditModalOpen(false);
      setActiveProduct(null);
      await loadProducts();
    } catch (err) {
      error(err.message, 'Update Failed');
    } finally {
      setIsSubmitting(false);
    }
  };

  // Submit Stock Update
  const handleUpdateStockSubmit = async (e) => {
    e.preventDefault();
    if (!activeProduct) return;

    const newStock = parseInt(stockInput, 10);
    if (Number.isNaN(newStock) || newStock < 0) {
      warning('Stock quantity must be a non-negative number.', 'Invalid Stock');
      return;
    }

    try {
      setIsSubmitting(true);
      await productsService.updateStock(activeProduct.id, newStock);
      success(`Stock for "${activeProduct.name}" set to ${newStock}.`, 'Inventory Adjusted');
      setIsStockModalOpen(false);
      setActiveProduct(null);
      await loadProducts();
    } catch (err) {
      error(err.message, 'Stock Adjustment Failed');
    } finally {
      setIsSubmitting(false);
    }
  };

  // Submit Delete Product
  const handleDeleteProduct = async () => {
    if (!activeProduct) return;

    try {
      setIsSubmitting(true);
      await productsService.delete(activeProduct.id);
      success(`Product "${activeProduct.name}" removed from catalog.`, 'Product Deleted');
      setIsDeleteModalOpen(false);
      setActiveProduct(null);
      await loadProducts();
    } catch (err) {
      error(err.message, 'Delete Failed');
    } finally {
      setIsSubmitting(false);
    }
  };

  // Table Columns Definition
  const tableColumns = [
    {
      key: 'sku',
      header: 'SKU',
      width: '130px',
      sortable: true,
      render: (val) => <span className="product-sku-pill">{val}</span>,
    },
    {
      key: 'name',
      header: 'Product Name',
      sortable: true,
      render: (val, row) => (
        <div>
          <span className="strong block">{val}</span>
          <span className="text-muted text-xs">{row.category}</span>
        </div>
      ),
    },
    {
      key: 'productType',
      header: 'Type',
      width: '130px',
      sortable: true,
      render: (val) => <StatusBadge status={val} size="sm" />,
    },
    {
      key: 'price',
      header: 'Price',
      width: '110px',
      align: 'right',
      sortable: true,
      render: (val) => <span className="strong">{formatCurrency(val)}</span>,
    },
    {
      key: 'stockQuantity',
      header: 'Stock',
      width: '130px',
      align: 'center',
      sortable: true,
      render: (val) => {
        const isLow = val <= (settings.lowStockThreshold || 10);
        const isOut = val === 0;
        return (
          <span
            className={`stock-indicator ${
              isOut ? 'stock-zero' : isLow ? 'stock-low' : 'stock-good'
            }`}
          >
            {isLow && <AlertTriangle size={12} className="stock-warn-icon" />}
            {isOut ? 'Out of Stock' : `${val} units`}
          </span>
        );
      },
    },
    {
      key: 'actions',
      header: 'Actions',
      width: '140px',
      align: 'right',
      render: (_, row) => (
        <div className="table-row-actions">
          <button
            type="button"
            className="btn-icon-subtle"
            onClick={() => handleOpenStock(row)}
            title="Adjust Stock"
          >
            <Package size={15} />
          </button>
          <button
            type="button"
            className="btn-icon-subtle"
            onClick={() => handleOpenEdit(row)}
            title="Edit Product"
          >
            <Edit2 size={15} />
          </button>
          <button
            type="button"
            className="btn-icon-subtle text-danger"
            onClick={() => handleOpenDelete(row)}
            title="Delete Product"
          >
            <Trash2 size={15} />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="page-container products-page">
      {/* Header Bar */}
      <div className="page-header">
        <div>
          <h2 className="page-title">Products Catalog</h2>
          <p className="page-subtitle">
            Manage SKU listings, physical inventory, digital downloads, and subscription services.
          </p>
        </div>
        <button
          type="button"
          className="btn btn-primary"
          onClick={() => {
            setFormData(INITIAL_FORM);
            setIsAddModalOpen(true);
          }}
        >
          <Plus size={16} />
          <span>Add Product</span>
        </button>
      </div>

      {/* Control Bar: Search & Filters */}
      <div className="controls-bar">
        <div className="controls-left">
          <SearchBar
            value={searchQuery}
            onChange={setSearchQuery}
            placeholder="Search by SKU or product name..."
            className="search-flex"
          />

          <FilterDropdown
            label="Category Filter"
            value={selectedCategory}
            onChange={setSelectedCategory}
            options={categories}
            allLabel="All Categories"
          />

          <FilterDropdown
            label="Type Filter"
            value={selectedType}
            onChange={setSelectedType}
            options={[
              { value: 'PHYSICAL', label: 'Physical Products' },
              { value: 'DIGITAL', label: 'Digital Assets' },
              { value: 'SUBSCRIPTION', label: 'Subscriptions' },
            ]}
            allLabel="All Product Types"
          />

          <FilterDropdown
            label="Stock Filter"
            value={stockFilter}
            onChange={setStockFilter}
            options={[
              { value: 'LOW', label: 'Low Stock (≤10)' },
              { value: 'OUT', label: 'Out of Stock (0)' },
            ]}
            allLabel="All Stock Levels"
          />
        </div>

        <div className="controls-right">
          <div className="view-mode-toggle">
            <button
              type="button"
              className={`view-btn ${viewMode === 'grid' ? 'view-btn-active' : ''}`}
              onClick={() => setViewMode('grid')}
              aria-label="Grid View"
              title="Card Grid View"
            >
              <Grid size={16} />
            </button>
            <button
              type="button"
              className={`view-btn ${viewMode === 'table' ? 'view-btn-active' : ''}`}
              onClick={() => setViewMode('table')}
              aria-label="Table View"
              title="Table View"
            >
              <List size={16} />
            </button>
          </div>
        </div>
      </div>

      {/* Main Content Area */}
      {isLoading ? (
        <LoadingSpinner message="Loading catalog..." fullHeight />
      ) : filteredProducts.length === 0 ? (
        <EmptyState
          title="No products match your criteria"
          description="Try relaxing your category or stock filter, or add a new product."
          actionLabel="Add New Product"
          onAction={() => {
            setFormData(INITIAL_FORM);
            setIsAddModalOpen(true);
          }}
        />
      ) : viewMode === 'grid' ? (
        <div className="products-grid">
          {filteredProducts.map((product) => (
            <ProductCard
              key={product.id}
              product={product}
              onEdit={handleOpenEdit}
              onDelete={handleOpenDelete}
              onUpdateStock={handleOpenStock}
              lowStockThreshold={settings.lowStockThreshold || 10}
            />
          ))}
        </div>
      ) : (
        <DataTable
          columns={tableColumns}
          data={filteredProducts}
          keyField="id"
          pageSize={10}
          initialSortField="name"
        />
      )}

      {/* Add Product Modal */}
      <Modal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        title="Add New Catalog Product"
        subtitle="Registers product entity in accordance with domain model"
        size="lg"
      >
        <form onSubmit={handleCreateProduct} className="modal-form">
          <div className="form-grid-two">
            <div className="form-group">
              <label className="form-label" htmlFor="sku">
                SKU Code <span className="text-danger">*</span>
              </label>
              <input
                id="sku"
                type="text"
                className="form-input"
                placeholder="e.g. PRD-5001"
                value={formData.sku}
                onChange={(e) => setFormData({ ...formData, sku: e.target.value })}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="name">
                Product Name <span className="text-danger">*</span>
              </label>
              <input
                id="name"
                type="text"
                className="form-input"
                placeholder="e.g. Wireless Noise-Cancelling Earbuds"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                required
              />
            </div>
          </div>

          <div className="form-grid-three">
            <div className="form-group">
              <label className="form-label" htmlFor="category">
                Category <span className="text-danger">*</span>
              </label>
              <input
                id="category"
                type="text"
                className="form-input"
                placeholder="e.g. Electronics"
                value={formData.category}
                onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="price">
                Price ($) <span className="text-danger">*</span>
              </label>
              <input
                id="price"
                type="number"
                step="0.01"
                min="0.01"
                className="form-input"
                placeholder="0.00"
                value={formData.price}
                onChange={(e) => setFormData({ ...formData, price: e.target.value })}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="stock">
                Stock Quantity <span className="text-danger">*</span>
              </label>
              <input
                id="stock"
                type="number"
                min="0"
                className="form-input"
                placeholder="0"
                value={formData.stockQuantity}
                onChange={(e) => setFormData({ ...formData, stockQuantity: e.target.value })}
                required
              />
            </div>
          </div>

          {/* Product Type Selector */}
          <div className="form-group">
            <label className="form-label">
              Product Classification (Inheritance Strategy) <span className="text-danger">*</span>
            </label>
            <div className="type-radio-group">
              {[
                { type: 'PHYSICAL', label: 'Physical Item', desc: 'Ships physically, has weight' },
                { type: 'DIGITAL', label: 'Digital Asset', desc: 'Instant URL download, zero shipping' },
                {
                  type: 'SUBSCRIPTION',
                  label: 'Subscription',
                  desc: 'Recurring term, zero shipping',
                },
              ].map((item) => (
                <label
                  key={item.type}
                  className={`type-radio-card ${
                    formData.productType === item.type ? 'active' : ''
                  }`}
                >
                  <input
                    type="radio"
                    name="productType"
                    value={item.type}
                    checked={formData.productType === item.type}
                    onChange={(e) => setFormData({ ...formData, productType: e.target.value })}
                  />
                  <div className="radio-text">
                    <span className="radio-title">{item.label}</span>
                    <span className="radio-desc">{item.desc}</span>
                  </div>
                </label>
              ))}
            </div>
          </div>

          {/* Type-Specific Domain Fields */}
          {formData.productType === 'PHYSICAL' && (
            <div className="form-group bg-subtle p-3 rounded">
              <label className="form-label" htmlFor="weightKg">
                Shipping Weight (kg)
              </label>
              <input
                id="weightKg"
                type="number"
                step="0.01"
                min="0.01"
                className="form-input"
                value={formData.weightKg}
                onChange={(e) => setFormData({ ...formData, weightKg: e.target.value })}
              />
              <span className="text-muted text-xs mt-1 block">
                Calculates shipping fee: $5.00 base + (weight × $2.50/kg).
              </span>
            </div>
          )}

          {formData.productType === 'DIGITAL' && (
            <div className="form-group bg-subtle p-3 rounded">
              <label className="form-label" htmlFor="downloadUrl">
                Asset Download URL
              </label>
              <input
                id="downloadUrl"
                type="url"
                className="form-input"
                placeholder="https://assets.apexcommerce.com/download/file.zip"
                value={formData.downloadUrl}
                onChange={(e) => setFormData({ ...formData, downloadUrl: e.target.value })}
              />
            </div>
          )}

          {formData.productType === 'SUBSCRIPTION' && (
            <div className="form-group bg-subtle p-3 rounded">
              <label className="form-label" htmlFor="billingPeriodMonths">
                Billing Cycle (Months)
              </label>
              <select
                id="billingPeriodMonths"
                className="form-input"
                value={formData.billingPeriodMonths}
                onChange={(e) => setFormData({ ...formData, billingPeriodMonths: e.target.value })}
              >
                <option value="1">Monthly (1 Month)</option>
                <option value="3">Quarterly (3 Months)</option>
                <option value="6">Biannual (6 Months)</option>
                <option value="12">Annual (12 Months)</option>
              </select>
            </div>
          )}

          <div className="modal-actions-right mt-4">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setIsAddModalOpen(false)}
            >
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
              {isSubmitting ? 'Saving...' : 'Add to Catalog'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Edit Product Modal */}
      <Modal
        isOpen={isEditModalOpen}
        onClose={() => setIsEditModalOpen(false)}
        title={`Edit Product: ${formData.name}`}
        subtitle={`SKU: ${formData.sku}`}
        size="lg"
      >
        <form onSubmit={handleUpdateProduct} className="modal-form">
          <div className="form-grid-two">
            <div className="form-group">
              <label className="form-label" htmlFor="edit-name">
                Product Name
              </label>
              <input
                id="edit-name"
                type="text"
                className="form-input"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="edit-category">
                Category
              </label>
              <input
                id="edit-category"
                type="text"
                className="form-input"
                value={formData.category}
                onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                required
              />
            </div>
          </div>

          <div className="form-grid-two">
            <div className="form-group">
              <label className="form-label" htmlFor="edit-price">
                Price ($)
              </label>
              <input
                id="edit-price"
                type="number"
                step="0.01"
                min="0.01"
                className="form-input"
                value={formData.price}
                onChange={(e) => setFormData({ ...formData, price: e.target.value })}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="edit-stock">
                Stock Quantity
              </label>
              <input
                id="edit-stock"
                type="number"
                min="0"
                className="form-input"
                value={formData.stockQuantity}
                onChange={(e) => setFormData({ ...formData, stockQuantity: e.target.value })}
                required
              />
            </div>
          </div>

          {formData.productType === 'PHYSICAL' && (
            <div className="form-group">
              <label className="form-label" htmlFor="edit-weightKg">
                Weight (kg)
              </label>
              <input
                id="edit-weightKg"
                type="number"
                step="0.01"
                className="form-input"
                value={formData.weightKg}
                onChange={(e) => setFormData({ ...formData, weightKg: e.target.value })}
              />
            </div>
          )}

          {formData.productType === 'DIGITAL' && (
            <div className="form-group">
              <label className="form-label" htmlFor="edit-downloadUrl">
                Download URL
              </label>
              <input
                id="edit-downloadUrl"
                type="url"
                className="form-input"
                value={formData.downloadUrl}
                onChange={(e) => setFormData({ ...formData, downloadUrl: e.target.value })}
              />
            </div>
          )}

          {formData.productType === 'SUBSCRIPTION' && (
            <div className="form-group">
              <label className="form-label" htmlFor="edit-billingPeriod">
                Billing Cycle (Months)
              </label>
              <select
                id="edit-billingPeriod"
                className="form-input"
                value={formData.billingPeriodMonths}
                onChange={(e) => setFormData({ ...formData, billingPeriodMonths: e.target.value })}
              >
                <option value="1">1 Month</option>
                <option value="3">3 Months</option>
                <option value="6">6 Months</option>
                <option value="12">12 Months</option>
              </select>
            </div>
          )}

          <div className="modal-actions-right mt-4">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setIsEditModalOpen(false)}
            >
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
              {isSubmitting ? 'Updating...' : 'Save Changes'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Adjust Stock Modal */}
      <Modal
        isOpen={isStockModalOpen}
        onClose={() => setIsStockModalOpen(false)}
        title="Adjust Inventory Stock"
        subtitle={activeProduct ? `${activeProduct.name} (${activeProduct.sku})` : ''}
        size="sm"
      >
        <form onSubmit={handleUpdateStockSubmit} className="modal-form">
          <div className="form-group">
            <label className="form-label" htmlFor="stockInput">
              New Stock Count
            </label>
            <input
              id="stockInput"
              type="number"
              min="0"
              className="form-input"
              value={stockInput}
              onChange={(e) => setStockInput(e.target.value)}
              autoFocus
              required
            />
            <span className="text-muted text-xs mt-1 block">
              Previous count: {activeProduct?.stockQuantity || 0} units
            </span>
          </div>

          <div className="modal-actions-right mt-4">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setIsStockModalOpen(false)}
            >
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
              {isSubmitting ? 'Updating...' : 'Confirm Stock'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Delete Confirmation Modal */}
      <Modal
        isOpen={isDeleteModalOpen}
        onClose={() => setIsDeleteModalOpen(false)}
        title="Confirm Product Deletion"
        size="sm"
        footer={
          <div className="modal-actions-right">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setIsDeleteModalOpen(false)}
            >
              Cancel
            </button>
            <button
              type="button"
              className="btn btn-danger"
              onClick={handleDeleteProduct}
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Deleting...' : 'Delete Product'}
            </button>
          </div>
        }
      >
        <div className="delete-confirm-body">
          <AlertTriangle size={36} className="text-danger mb-2" />
          <p>
            Are you sure you want to permanently remove <strong>{activeProduct?.name}</strong> (SKU:{' '}
            {activeProduct?.sku})?
          </p>
          <p className="text-muted text-xs mt-1">This action cannot be undone.</p>
        </div>
      </Modal>
    </div>
  );
}
