const express = require('express');
const cors = require('cors');

const app = express();

// Middlewares
app.use(express.json());
app.use(cors()); // السماح بالاتصال بالسيرفر من أي مكان (تطبيقات الأندرويد والويب)

// 1. المسار الرئيسي (Root Route) - يحل مشكلة 404 عند فتح الرابط المباشر
app.get('/', (req, res) => {
    res.status(200).json({
        status: "success",
        message: "Delivery Backend API is Running Successfully!",
        endpoints: {
            getProducts: "GET /api/products",
            createOrder: "POST /api/orders"
        }
    });
});

// قائمة المنتجات التي سيتم عرضها في التطبيق
const products = [
    { id: "1", name: "وجبة برجر سبيشل", price: 12.5, image: "https://via.placeholder.com/150", available: true },
    { id: "2", name: "بيتزا ببروني عائلية", price: 18.0, image: "https://via.placeholder.com/150", available: true },
    { id: "3", name: "وجبة شاورما عربي", price: 8.0, image: "https://via.placeholder.com/150", available: true },
    { id: "4", name: "عصير برتقال طبيعي", price: 3.5, image: "https://via.placeholder.com/150", available: true }
];

// 2. رابط جلب جميع المنتجات المتاحة
app.get('/api/products', (req, res) => {
    res.json(products);
});

// 3. رابط إرسال طلب جديد
app.post('/api/orders', (req, res) => {
    const { productId, quantity, userLocation, userPhone } = req.body;

    res.json({
        status: "success",
        message: "تم استلام طلبك بنجاح وسيتم التواصل معك لترتيب التوصيل!",
        orderId: "ORD-" + Math.floor(1000 + Math.random() * 9000)
    });
});

// تصدير التطبيق ليعمل على Vercel Serverless Functions
module.exports = app;

// تشغيل السيرفر عند التطوير المحلي
const PORT = process.env.PORT || 3000;
if (process.env.NODE_ENV !== 'production') {
    app.listen(PORT, () => console.log(`Server is running locally on port ${PORT}`));
}