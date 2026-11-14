import { customers } from './customers';
import { products } from './products';
import { buildOrder } from '../utils/pricing';

const product = Object.fromEntries(products.map((item) => [item.sku, item]));
const customer = Object.fromEntries(customers.map((item) => [item.id, item]));

export const orders = [
  buildOrder({
    id: 1,
    orderNumber: 'ORD-2026-1001',
    customer: customer[1],
    status: 'DELIVERED',
    createdAt: '2026-06-12T10:24:00',
    lines: [
      { product: product['AWH-1001'], quantity: 1 },
      { product: product['LRP-3001'], quantity: 1 },
    ],
  }),
  buildOrder({
    id: 2,
    orderNumber: 'ORD-2026-1002',
    customer: customer[2],
    status: 'PROCESSING',
    createdAt: '2026-11-02T09:18:00',
    lines: [{ product: product['PRS-2088'], quantity: 2 }],
  }),
  buildOrder({
    id: 3,
    orderNumber: 'ORD-2026-1003',
    customer: customer[3],
    status: 'SHIPPED',
    createdAt: '2026-10-28T15:42:00',
    lines: [
      { product: product['LDL-1104'], quantity: 1 },
      { product: product['OSB-1522'], quantity: 1 },
    ],
  }),
  buildOrder({
    id: 4,
    orderNumber: 'ORD-2026-1004',
    customer: customer[5],
    status: 'PENDING',
    createdAt: '2026-11-08T11:05:00',
    lines: [{ product: product['SMK-1760'], quantity: 1 }],
  }),
  buildOrder({
    id: 5,
    orderNumber: 'ORD-2026-1005',
    customer: customer[4],
    status: 'DELIVERED',
    createdAt: '2026-09-18T13:30:00',
    lines: [{ product: product['FNC-3340'], quantity: 1 }],
  }),
  buildOrder({
    id: 6,
    orderNumber: 'ORD-2026-1006',
    customer: customer[8],
    status: 'CANCELLED',
    createdAt: '2026-08-03T16:12:00',
    lines: [{ product: product['HCS-1412'], quantity: 1 }],
  }),
  buildOrder({
    id: 7,
    orderNumber: 'ORD-2026-1007',
    customer: customer[6],
    status: 'DELIVERED',
    createdAt: '2026-07-22T08:48:00',
    lines: [
      { product: product['NWC-2208'], quantity: 1 },
      { product: product['BKT-3008'], quantity: 1 },
    ],
  }),
  buildOrder({
    id: 8,
    orderNumber: 'ORD-2026-1008',
    customer: customer[7],
    status: 'PROCESSING',
    createdAt: '2026-11-06T19:20:00',
    lines: [{ product: product['ACP-4101'], quantity: 1 }],
  }),
  buildOrder({
    id: 9,
    orderNumber: 'ORD-2026-1009',
    customer: customer[9],
    status: 'SHIPPED',
    createdAt: '2026-10-11T12:10:00',
    lines: [
      { product: product['ASA-4112'], quantity: 1 },
      { product: product['LRP-3001'], quantity: 2 },
    ],
  }),
  buildOrder({
    id: 10,
    orderNumber: 'ORD-2026-1010',
    customer: customer[10],
    status: 'DELIVERED',
    createdAt: '2026-06-30T14:55:00',
    lines: [
      { product: product['AWH-1001'], quantity: 1 },
      { product: product['PRS-2088'], quantity: 1 },
    ],
  }),
  buildOrder({
    id: 11,
    orderNumber: 'ORD-2026-1011',
    customer: customer[11],
    status: 'PENDING',
    createdAt: '2026-11-09T10:40:00',
    lines: [{ product: product['SMK-1760'], quantity: 2 }],
  }),
  buildOrder({
    id: 12,
    orderNumber: 'ORD-2026-1012',
    customer: customer[12],
    status: 'PROCESSING',
    createdAt: '2026-11-01T07:25:00',
    lines: [{ product: product['OSB-1522'], quantity: 2 }],
  }),
  buildOrder({
    id: 13,
    orderNumber: 'ORD-2026-1013',
    customer: customer[1],
    status: 'DELIVERED',
    createdAt: '2026-08-19T18:05:00',
    lines: [{ product: product['LDL-1104'], quantity: 5 }],
  }),
  buildOrder({
    id: 14,
    orderNumber: 'ORD-2026-1014',
    customer: customer[5],
    status: 'DELIVERED',
    createdAt: '2026-07-02T09:14:00',
    lines: [{ product: product['LRP-3001'], quantity: 1 }],
  }),
  buildOrder({
    id: 15,
    orderNumber: 'ORD-2026-1015',
    customer: customer[6],
    status: 'SHIPPED',
    createdAt: '2026-10-20T16:36:00',
    lines: [
      { product: product['FNC-3340'], quantity: 1 },
      { product: product['NWC-2208'], quantity: 1 },
    ],
  }),
  buildOrder({
    id: 16,
    orderNumber: 'ORD-2026-1016',
    customer: customer[3],
    status: 'DELIVERED',
    createdAt: '2026-09-05T11:48:00',
    lines: [{ product: product['PRS-2088'], quantity: 6 }],
  }),
  buildOrder({
    id: 17,
    orderNumber: 'ORD-2026-1017',
    customer: customer[4],
    status: 'PENDING',
    createdAt: '2026-11-10T08:12:00',
    lines: [
      { product: product['ASA-4112'], quantity: 1 },
      { product: product['ACP-4101'], quantity: 1 },
    ],
  }),
  buildOrder({
    id: 18,
    orderNumber: 'ORD-2026-1018',
    customer: customer[2],
    status: 'CANCELLED',
    createdAt: '2026-06-02T13:22:00',
    lines: [{ product: product['HCS-1412'], quantity: 2 }],
  }),
];
