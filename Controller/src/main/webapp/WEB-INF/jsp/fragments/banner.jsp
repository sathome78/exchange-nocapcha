<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<article class="banner-wr">
	<link href='https://fonts.googleapis.com/css?family=Roboto:700,600,500,400,200,100' rel='stylesheet' type='text/css'>
	<style>

		.banner-wr{
			width: 100%;
			font-size: 20px;
			background-color: #262151;
			font-family: Roboto;
			font-weight: 500;
			padding-left: 20px;
			padding-right: 20px;
			height: 3em;
			overflow: hidden;
		}

		.banner-wr .banner-content-inner:nth-child(1){
			transform: translateY(5.2em);
			/*animation: 20s an1 10s infinite;*/
		}
		.banner-wr .banner-content-inner:nth-child(2){
			transform: translateY(-5.2em);
			position: absolute;
			top: 0;
			bottom: 0;
			right: 0;
			left: 0;
			/*animation: 20s an2 10s infinite;*/
		}
		.banner-wr .banner-content-inner{
			transform: translateY(-5.2em);
			position: absolute;
			top: 0;
			bottom: 0;
			right: 0;
			left: 0;
		}
		.banner-wr .banner-content-inner.active{
			transform: translateY(0);
		}
		.banner-wr .banner-content{
			height: 3em;
			position: relative;
		}
		.banner-wr .banner-content-inner{
			display: flex;
			justify-content: center;
			align-items: center;
			padding-top: 2px;
			transition: .6s ease-out;
		}
		.banner-wr .banner-content-inner a{
			position: absolute;
			top: 0;
			right: 0;
			left: 0;
			bottom: 0;
			cursor: pointer;
			text-decoration: none;
			z-index: 3;
		}
		.banner-wr .banner-img{
			margin-right: .85em;
			height: calc(3em - 2px);
			padding-top: 2px;
			display: flex;
			align-items: center;

		}
		.banner-img  img{
			max-height: 100%;
			max-width: 100%;
			object-fit: contain;
			object-position: center;
			display: block;

		}
		.banner-wr .banner-name{
			margin-right: .75em;
		}
		.banner-name p{
			opacity: 0.3;
			color: #ffffff;
			font-size: .8em;
		}
		.banner-wr .youtube-pic{
			margin-right: .75em;
			height: .9em;
		}
		.youtube-pic img{
			height: 100%;
		}
		.banner-wr .banner-discription{
		}
		.banner-discription p{
			opacity: 0.3;
			color: #ffffff;
			font-size: .8em;
			padding-top: 4px;
		}
		@media(max-width: 768px){
			.banner-wr{
				font-size: 12px;
			}
			.banner-discription p{
				padding-top: 0;
			}
		}
	</style>
	<div class="banner-content">
		<div class="banner-content-inner active">
			<a href="https://youtu.be/6ei8Cr-jMBU" target="blank"></a>
			<div class="banner-img">
				<img src="<c:url value="/client/img/b-pic1.png"/>" alt=""/>
			</div>
			<div class="banner-name">
				<p>NakamotoJedi</p>
			</div>
			<div class="youtube-pic">
				<img src="<c:url value="/client/img/youtube-pic.png"/>" alt=""/>
			</div>
			<div class="banner-discription">
				<p>Episode #78 - Edge Cryptocurrency Wallet + Blockchain Platform — insights</p>
			</div>
		</div>
		<div class="banner-content-inner">
			<a href="https://youtu.be/K1yCJtkf3hk" target="blank"></a>
			<div class="banner-img">
				<img src="<c:url value="/client/img/b-pic2.png"/>" alt=""/>
			</div>
			<div class="banner-name">
				<p>NakamotoJedi</p>
			</div>
			<div class="youtube-pic">
				<img src="<c:url value="/client/img/youtube-pic.png"/>" alt=""/>
			</div>
			<div class="banner-discription">
				<p>Episode #79 - BTC $3.500 | No BTC ETF - SEC | Lose $8 Million in Crypto Scam</p>
			</div>
		</div>
		<div class="banner-content-inner">
			<a href="https://youtu.be/4Mx86-dtoNY" target="blank"></a>
			<div class="banner-img">
				<img src="<c:url value="/client/img/b-pic3.png"/>" alt=""/>
			</div>
			<div class="banner-name">
				<p>NakamotoJedi</p>
			</div>
			<div class="youtube-pic">
				<img src="<c:url value="/client/img/youtube-pic.png"/>" alt=""/>
			</div>
			<div class="banner-discription">
				<p>Episode #80 - Bitcoin SV creator Craig Wright — the real Satoshi Nakamoto?</p>
			</div>
		</div>
	</div>
</article>






<script>
	setInterval(function(){
		document.querySelector(".banner-content-inner").classList.remove("active");
		document.querySelector(".banner-content-inner").nextElementSibling.classList.add("active");
		setTimeout(function(){
			var firstBanner = document.querySelector(".banner-content >.banner-content-inner:first-child");
			var lastBanner = document.querySelector(".banner-content >.banner-content-inner:last-child");
			var parrentBanner = document.querySelector(".banner-content");
			parrentBanner.insertBefore(firstBanner,lastBanner.nextSibling);
		},1000)
	},20000)
</script>