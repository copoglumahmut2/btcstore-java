-- Add video_link column to product table
-- This allows products to have a video link (YouTube, Vimeo, etc.)

ALTER TABLE product ADD COLUMN video_link VARCHAR(1000);

-- Add comment
COMMENT ON COLUMN product.video_link IS 'Video link for product (YouTube, Vimeo, etc.)';
