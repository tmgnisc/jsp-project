<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Music Detail - Nepal Navigator</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            font-family: 'Poppins', sans-serif;
            background-color: #f5f7fa;
        }
        .youtube-player {
            width: 100%;
            height: 300px;
            border-radius: 8px;
            object-fit: cover;
        }
        .section-title {
            color: #1a3c70;
            font-weight: 600;
        }
        .link-button {
            color: #f4a300;
            text-decoration: none;
            transition: color 0.3s;
        }
        .link-button:hover {
            color: #a31621;
        }
    </style>
</head>
<body class="bg-gray-50">
    <!-- Navigation -->
    <nav class="bg-[#002B5B] text-white shadow-lg">
        <div class="container mx-auto px-4">
            <div class="flex justify-between items-center py-4">
                <a href="index" class="text-2xl font-bold text-[#F4A300]">Nepal Navigator</a>
                <div class="hidden md:flex space-x-6">
                    <a href="index" class="hover:text-[#F4A300]">Home</a>
                    <a href="foods" class="hover:text-[#F4A300]">Foods</a>
                    <a href="attractions" class="hover:text-[#F4A300]">Attractions</a>
                    <a href="music" class="hover:text-[#F4A300]">Music</a>
                    <a href="movies" class="hover:text-[#F4A300]">Movies</a>
                    <a href="sports" class="hover:text-[#F4A300]">Sports</a>
                    <% String username = (String) session.getAttribute("username");
                       if (username != null) { %>
                        <span class="text-white">Welcome, <%= username %>!</span>
                        <a href="logout" class="hover:text-[#F4A300]">Logout</a>
                    <% } else { %>
                        <a href="login" class="hover:text-[#F4A300]">Login/Register</a>
                    <% } %>
                </div>
                <button class="md:hidden">
                    <i class="fas fa-bars text-2xl"></i>
                </button>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="max-w-7xl mx-auto px-4 py-8">
        <!-- Breadcrumb -->
        <div class="mb-6 text-gray-600">
            <nav class="flex" aria-label="Breadcrumb">
                <ol class="inline-flex items-center space-x-2">
                    <li class="inline-flex items-center">
                        <a href="index" class="hover:text-[#F4A300]">Home</a>
                    </li>
                    <li class="inline-flex items-center">
                        <span class="mx-2">></span>
                        <a href="music" class="hover:text-[#F4A300]">Music</a>
                    </li>
                    <li class="inline-flex items-center">
                        <span class="mx-2">></span>
                        <span>${music.artistName}</span>
                    </li>
                </ol>
            </nav>
        </div>

        <!-- Music Detail -->
        <div class="bg-white rounded-lg shadow-lg overflow-hidden">
            <div class="p-6">
                <div class="flex items-center justify-between mb-4">
                    <h1 class="text-3xl font-bold text-[#1a3c70]">${music.artistName}</h1>
                    <button class="bg-white p-2 rounded-full shadow-lg hover:bg-gray-100">
                        <i class="fas fa-heart text-red-500"></i>
                    </button>
                </div>
                <div class="flex items-center mb-6">
                    <div class="flex text-yellow-400">
                        <i class="fas fa-star"></i>
                        <i class="fas fa-star"></i>
                        <i class="fas fa-star"></i>
                        <i class="fas fa-star"></i>
                        <i class="fas fa-star-half-alt"></i>
                    </div>
                    <span class="text-gray-600 ml-2">4.5 (180 reviews)</span>
                </div>

                <!-- YouTube Thumbnail -->
                <% String youtubeUrl = (String) request.getAttribute("music.youtubeChannelUrl");
                   String videoId = "";
                   String thumbnailUrl = "";
                   if (youtubeUrl != null) {
                       if (youtubeUrl.contains("watch?v=")) {
                           videoId = youtubeUrl.split("v=")[1].split("&")[0];
                       } else if (youtubeUrl.contains("youtu.be/")) {
                           videoId = youtubeUrl.split("youtu.be/")[1].split("\\?")[0];
                       }
                   }
                   if (!videoId.isEmpty()) {
                       thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
                   }
                %>
                <div class="mb-6">
                    <% if (!thumbnailUrl.isEmpty()) { %>
                        <img src="<%= thumbnailUrl %>" 
                             alt="${music.artistName} Thumbnail" 
                             class="youtube-player">
                    <% } else { %>
                        <img src="https://ui-avatars.com/api/?name=${music.artistName}&size=300" 
                             alt="${music.artistName} Fallback" 
                             class="youtube-player">
                    <% } %>
                </div>

                <!-- Information Sections -->
                <div class="space-y-4">
                    <div>
                        <h2 class="text-xl section-title">About the Artist</h2>
                        <p class="text-gray-600 mt-2">${music.description}</p>
                    </div>

                    <div>
                        <h2 class="text-xl section-title">Popular Songs</h2>
                        <p class="text-gray-600 mt-2">${music.popularSongs}</p>
                    </div>

                    <div>
                        <h2 class="text-xl section-title">Achievements</h2>
                        <p class="text-gray-600 mt-2">${music.achievements}</p>
                    </div>

                    <div>
                        <h2 class="text-xl section-title">YouTube Channel</h2>
                        <a href="${music.youtubeChannelUrl != null ? music.youtubeChannelUrl : '#'}" 
                           class="flex items-center mt-2 link-button">
                            <i class="fab fa-youtube mr-2"></i>
                            <span>Visit YouTube Channel</span>
                        </a>
                    </div>

                    <div>
                        <h2 class="text-xl section-title">Formation Year</h2>
                        <p class="text-gray-600 mt-2">${music.formationYear}</p>
                    </div>
                </div>
            </div>

            <!-- Comments Section -->
            <div class="border-t border-gray-200 p-8">
                <h2 class="text-2xl font-semibold text-[#1a3c70] mb-6">Comments</h2>
                
                <!-- Comment Form -->
                <div class="mb-8">
                    <form action="music-detail" method="post">
                        <input type="hidden" name="musicId" value="${music.id}">
                        <div>
                            <textarea name="commentText" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#F4A300]" 
                                      rows="3" placeholder="Write your comment..."></textarea>
                        </div>
                        <div class="flex justify-end mt-2">
                            <button type="submit" class="bg-[#F4A300] text-white px-6 py-2 rounded-md hover:bg-[#A31621] transition duration-300">
                                Post Comment
                            </button>
                        </div>
                        <% String error = (String) request.getAttribute("error");
                           if (error != null) { %>
                            <p class="text-red-500 mt-2"><%= error %></p>
                        <% } %>
                    </form>
                </div>

                <!-- Comments List -->
                <div class="space-y-6">
                    <%@ page import="java.util.List, model.MusicComment" %>
                    <% List<MusicComment> comments = (List<MusicComment>) request.getAttribute("comments");
                       if (comments != null && !comments.isEmpty()) {
                           for (MusicComment comment : comments) { %>
                    <div class="flex space-x-4">
                        <img src="https://ui-avatars.com/api/?name=<%= comment.getUsername() %>&background=002B5B&color=fff" 
                             alt="User" class="w-12 h-12 rounded-full">
                        <div class="flex-1">
                            <div class="flex items-center justify-between">
                                <h3 class="font-semibold text-[#1a3c70]"><%= comment.getUsername() %></h3>
                                <span class="text-sm text-gray-500"><%= comment.getCreatedAt() %></span>
                            </div>
                            <p class="text-gray-600 mt-1"><%= comment.getCommentText() %></p>
                            <div class="flex items-center space-x-4 mt-2">
                                <button class="text-gray-500 hover:text-[#F4A300]">
                                    <i class="far fa-thumbs-up"></i> Like
                                </button>
                                <button class="text-gray-500 hover:text-[#F4A300]">
                                    <i class="far fa-comment"></i> Reply
                                </button>
                            </div>
                        </div>
                    </div>
                    <% }
                       } else { %>
                       <p class="text-gray-500">No comments yet. Be the first to comment!</p>
                    <% } %>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="bg-[#002B5B] text-white mt-12">
        <div class="max-w-7xl mx-auto px-4 py-8">
            <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
                <div>
                    <h3 class="text-xl font-bold text-[#F4A300] mb-4">Nepal Navigator</h3>
                    <p class="text-gray-300">
                        Discover the rich culture, cuisine, and beauty of Nepal through our comprehensive guide.
                    </p>
                </div>
                <div>
                    <h4 class="text-lg font-semibold mb-4">Quick Links</h4>
                    <ul class="space-y-2">
                        <li><a href="foods" class="text-gray-300 hover:text-[#F4A300]">Foods</a></li>
                        <li><a href="scenery" class="text-gray-300 hover:text-[#F4A300]">Attractions</a></li>
                        <li><a href="music" class="text-gray-300 hover:text-[#F4A300]">Music</a></li>
                        <li><a href="movies" class="text-gray-300 hover:text-[#F4A300]">Movies</a></li>
                        <li><a href="sport" class="text-gray-300 hover:text-[#F4A300]">Sports</a></li>
                    </ul>
                </div>
                <div>
                    <h4 class="text-lg font-semibold mb-4">Contact Us</h4>
                    <ul class="space-y-2 text-gray-300">
                        <li><i class="fas fa-envelope mr-2"></i> info@nepalnavigator.com</li>
                        <li><i class="fas fa-phone mr-2"></i> +977 1234567890</li>
                        <li><i class="fas fa-map-marker-alt mr-2"></i> Kathmandu, Nepal</li>
                    </ul>
                </div>
                <div>
                    <h4 class="text-lg font-semibold mb-4">Follow Us</h4>
                    <div class="flex space-x-4">
                        <a href="#" class="text-gray-300 hover:text-[#F4A300]"><i class="fab fa-facebook-f"></i></a>
                        <a href="#" class="text-gray-300 hover:text-[#F4A300]"><i class="fab fa-twitter"></i></a>
                        <a href="#" class="text-gray-300 hover:text-[#F4A300]"><i class="fab fa-instagram"></i></a>
                        <a href="#" class="text-gray-300 hover:text-[#F4A300]"><i class="fab fa-youtube"></i></a>
                    </div>
                </div>
            </div>
            <div class="border-t border-gray-700 mt-8 pt-8 text-center text-gray-300">
                <p>© 2024 Nepal Navigator. All rights reserved.</p>
            </div>
        </div>
    </footer>
</body>
</html>