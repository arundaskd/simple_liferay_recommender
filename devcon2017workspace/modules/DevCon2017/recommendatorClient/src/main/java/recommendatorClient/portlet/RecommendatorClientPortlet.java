package recommendatorClient.portlet;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.blogs.kernel.service.BlogsEntryLocalServiceUtil;
import com.liferay.demo.SRecommendator;
import com.liferay.demo.Recommendator;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageLocalServiceUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

import fooservice.service.FooLocalServiceUtil;
import recommendatorClient.constants.RecommendatorClientPortletKeys;

/**
 * @author carlos
 */
@Component(immediate = true, property = { "com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true", "javax.portlet.display-name=recommendatorClient Portlet",
		"javax.portlet.init-param.template-path=/", "javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + RecommendatorClientPortletKeys.RecommendatorClient,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user" }, service = Portlet.class)
public class RecommendatorClientPortlet extends MVCPortlet {
	private static final String _BLOG_ = "(blog)";
	private static final String _WIKI_ = "(wiki)";
	private static final String JOURNAL_ARTICLE = "com.liferay.journal.model.JournalArticle";
	private static final String WIKI_PAGE = "com.liferay.wiki.model.WikiPage";
	private static final String BLOGS_ENTRY = "com.liferay.blogs.kernel.model.BlogsEntry";

	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {

		LiferayPortletRequest portletRequest = PortalUtil.getLiferayPortletRequest(renderRequest);
		LiferayPortletResponse portletResponse = PortalUtil.getLiferayPortletResponse(renderResponse);

		User user = (User) renderRequest.getAttribute(WebKeys.USER);
		if (user == null) return;
		AssetRendererFactory<BlogsEntry> blogsAssetRendererFactory = AssetRendererFactoryRegistryUtil
				.getAssetRendererFactoryByClass(BlogsEntry.class);
		AssetRendererFactory<WikiPage> wikiAssetRendererFactory = AssetRendererFactoryRegistryUtil
				.getAssetRendererFactoryByClass(WikiPage.class);
		AssetRendererFactory<JournalArticle> journalArticleAssetRendererFactory = AssetRendererFactoryRegistryUtil
				.getAssetRendererFactoryByClass(JournalArticle.class);

		Map<String, String> result = new HashMap<String, String>();
		for (Map.Entry<String, Double> e : getRecommendations(user).entrySet()) {
			String k = e.getKey();
			Double v = e.getValue();
			AssetRenderer<?> ar = null;
			System.out.println(String.format("\n\n%s-%s\n\n", k, v));
			String url = null;
			try {
				switch (k.split("-")[0]) {
				case BLOGS_ENTRY:
					BlogsEntry be = BlogsEntryLocalServiceUtil.getEntry(Long.parseLong(k.split("-")[1]));
					ar = blogsAssetRendererFactory.getAssetRenderer(be.getTrashEntryClassPK(), 1);
					url = ar.getURLViewInContext(portletRequest, portletResponse, null);
					result.put(be.getTitle() + _BLOG_, url);
					break;
				case WIKI_PAGE:
					WikiPage we = WikiPageLocalServiceUtil.getPageByPageId(Long.parseLong(k.split("-")[1]));
					ar = wikiAssetRendererFactory.getAssetRenderer(we.getTrashEntryClassPK(), 1);
					url = ar.getURLViewInContext(portletRequest, portletResponse, null);
					result.put(we.getTitle() + _WIKI_, url);
					break;
				case JOURNAL_ARTICLE:
					JournalArticle ja = JournalArticleLocalServiceUtil
							.getLatestArticle(Long.parseLong(k.split("-")[1]));
					ar = journalArticleAssetRendererFactory.getAssetRenderer(ja.getTrashEntryClassPK(), 1);
					url = ar.getURLViewInContext(portletRequest, portletResponse, null);
					result.put(ja.getTitle(renderRequest.getLocale()), url);
					break;
				default:
					break;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		renderRequest.setAttribute("urls", result);
		System.out.println(result);
		include(viewTemplate, renderRequest, renderResponse);
	}

	private Map<String, Double> getRecommendations(User user) {
		Map<Long, Map<String, Integer>> ratings = new HashMap();
		FooLocalServiceUtil.getAllBehaviour().forEach((e) -> {
			Object[] x = ((Object[]) e);
			Long u = ((BigInteger) x[2]).longValue();
			String document = String.format("%s-%s", x[0], x[1]);
			if (ratings.get(u) == null)
				ratings.put(u, new Hashtable<String, Integer>());
			Integer times = ratings.get(u).get(document);
			ratings.get(u).put(document, ((BigInteger) x[3]).intValue() + (times == null ? 0 : times));
			System.out.println(String.format("%s->%s=%s",u,document,x[3]));
		});
		System.out.println("\n\n"+ratings+"\n\n");
		Map<String, Double> recommendations = 
				SRecommendator.recommend(new Long(user.getUserId()), ratings);
		System.out.println("\n\n" + recommendations + "\n\n");
		return recommendations;
	}

}